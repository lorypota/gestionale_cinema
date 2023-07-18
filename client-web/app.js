const API_URL = "http://localhost:8080";

//variabili globali
var projections;
var movies;
var loadedProjection = undefined;
var selectedSeats = [];

//azioni su caricamento del documento
document.addEventListener("DOMContentLoaded", async () => {

  document.getElementById('booking-section').style.display = 'none';
  document.getElementById('seating-section').style.display = 'none';
  document.getElementById('insert-booking-informations').style.display = 'none';
  document.getElementById('seating-section-modify').style.display = 'none';
  
  projections = await getAllProjections();
  movies = await getAllMovies();

  //mostra protiezioni
  movies.forEach(async movie => {
    const projLabel = document.createElement('button');
    projLabel.className = 'projLabel';

    const title = document.createElement('div');
    title.className = 'projTitle';
    title.innerText = movie.name;

    const duration = document.createElement('div');
    duration.className = 'projDuration';
    duration.innerText = "Durata: " + movie.duration;

    projLabel.append(title, duration);
    projLabel.style.backgroundImage = `url('${movie.image}')`;
    projLabel.style.backgroundSize = "cover";

    projLabel.addEventListener("click", async () => {
      document.getElementById('booking-section').style.display = 'block';
      resetSeats();
      loadProjections(movie.id);
    });

    document.getElementById('projections-container').append(projLabel);
  });
  
  document.getElementById('projections-selection').addEventListener('change', async function() {
    const selectedProjection = Number(this.value);
    if(selectedProjection === -1){
      resetSeats();
    } else {
      await showSeats(selectedProjection);
    }
  });

  document.querySelector('#booking-management-field').addEventListener('keydown', function(event) {
    if (event.keyCode === 13) {  // 13 è il keyCode per il tasto Invio
      event.preventDefault();
    }
  });

  document.querySelector('#booking-management-field').addEventListener('change', async function() {
    const bookingId = Number(this.value);
    const booking = await getBookingById(bookingId);
  
    if(projections.some(proj => proj.id === booking.proj_id)){
      document.querySelector('#update-booking').style.display = 'block';
    } else {
      document.querySelector('#update-booking').style.display = 'none';
    }
  
    await loadBooking(bookingId);
  });

  // Quando l'utente clicca sul bottone, apro il modal 
  document.querySelector("#insert-booking-informations").addEventListener('click', function(event) {
    event.preventDefault();
    document.body.style.overflow = "hidden"; // Nasconde la barra di scorrimento
  
    selectedSeats = [];
    let seats = document.querySelectorAll('#seating-container .seat.selected');
    seats.forEach(function(seat){
      selectedSeats.push(seat.textContent);
    });
  
    document.querySelector("#selected-seats").textContent = selectedSeats.join(', ');
    document.querySelector("#booking-infos").style.display = 'block';
  });

  // Quando l'utente clicca su <span> (x), chiudo il modal
  document.querySelectorAll(".close").forEach(closeButton => closeButton.addEventListener('click', closeModal));

  // Quando l'utente clicca ovunque fuori dal modal, lo chiudo
  window.addEventListener('click', function(event) {
    if (event.target === document.querySelector("#booking-infos")) {
      closeModal();
    }
  });

  function closeModal() {
    document.body.style.overflow = "auto"; // Mostra la barra di scorrimento
    document.querySelector("#booking-infos").style.display = 'none';
  }

  document.getElementById('submit-booking').addEventListener('click', async (event) => {
    event.preventDefault();

    if(loadedProjection === undefined)
      throw new Error('Non è stata selezionata alcuna proiezione');
    
    selectedSeatsTransformed = selectedSeats.map(seat => {
      const column = seat.charCodeAt(0) - 'A'.charCodeAt(0) + 1;
      const row = seat.slice(1);
      return { "row": Number(row), "column": Number(column) };
    })

    let newBooking = {
      "proj_id": loadedProjection,
      "seats": selectedSeatsTransformed,
      "name": document.querySelector('#fname').value,
      "surname": document.querySelector('#lname').value,
      "email": document.querySelector('#email').value
    }
    
    res = await addBooking(newBooking);

    switch (res.status) {
      case 201: 
        res = await res.json();
        alert(`Booking has been completed with success! Your booking id is: ${res.id}`);
        break;
      case 409: 
        alert("Booking failed since seats have already been booked!"); 
        location.reload();
        break;
      case 400: 
        alert("Booking failed due to missing fields!");
        location.reload();
        break;
      case 500: 
        alert("Booking failed due to server error!");
        location.reload();
        break;
    }

    closeModal();
    resetSeats();
    document.querySelector('#booking-section').style.display = "none";

    document.querySelector("#booking-management-field").value = res.id;
    loadBooking(res.id);
  });

  document.getElementById('update-booking').addEventListener('click', async (event) => {
    let oldBooking = await getBookingById(document.querySelector('#booking-management-field').value);
    
    document.querySelector('#update-booking').style.display = 'none';
    document.querySelector('#booking-seats').style.display = 'none';
    document.querySelector('#submit-changes').style.display = 'block';
    document.querySelector('#seating-section-modify').style.display = 'block';

    document.querySelector('#booking-name').outerHTML = `<input type='text' id='booking-name' value='${document.querySelector('#booking-name').textContent}'>`;
    document.querySelector('#booking-surname').outerHTML = `<input type='text' id='booking-surname' value='${document.querySelector('#booking-surname').textContent}'>`;
    document.querySelector('#booking-email').outerHTML = `<input type='text' id='booking-email' value='${document.querySelector('#booking-email').textContent}'>`;

    showSeats(oldBooking.proj_id, oldBooking);
  })

  document.querySelector('#submit-changes').addEventListener('click', async function(event){
    event.preventDefault();

    if(loadedProjection === undefined)
      throw new Error('Non è stata selezionata alcuna proiezione');

    selectedSeats = [];
    
    let tempSeats = document.querySelectorAll('#seating-container-modify .seat.selected');
    tempSeats.forEach(function(seat) {
      selectedSeats.push(seat.textContent);
    });

    selectedSeatsTransformed = selectedSeats.map(seat => {
      const column = seat.charCodeAt(0) - 'A'.charCodeAt(0) + 1;
      const row = seat.slice(1);
      return { "row": Number(row), "column": Number(column) };
    })
    
    let oldBooking = await getBookingById(document.getElementById('booking-management-field').value);
    let idBooking = oldBooking.id;

    delete oldBooking.id;

    let newBooking = {
      "proj_id": loadedProjection,
      "seats": selectedSeatsTransformed,
      "name": document.getElementById('booking-name').value,
      "surname": document.getElementById('booking-surname').value,
      "email": document.getElementById('booking-email').value
    }

    let res = await updateBooking(idBooking, newBooking);

    switch (res.status){
      case 204: 
        alert(`Booking update has been completed with success!`);
        break;
      case 404: 
        alert("Booking update failed due to id not found!"); 
        location.reload();
        break;
      case 400: 
        alert("Booking update failed due to missing fields!");
        location.reload();
        break;
      case 500: 
        alert("Booking update failed due to server error!");
        location.reload();
        break;
    }

    resetSeats();

    document.getElementById('booking-management-field').value = idBooking;
    loadBooking(idBooking);

    document.querySelector('#update-booking').style.display = 'block';
    document.querySelector('#booking-seats').style.display = 'block';
    document.querySelector('#submit-changes').style.display = 'none';
    document.querySelector('#seating-section-modify').style.display = 'none';

    document.querySelector('#booking-name').outerHTML = `<span id='booking-name'>${document.querySelector('#booking-name').value}</span>`;
    document.querySelector('#booking-surname').outerHTML = `<span id='booking-surname'>${document.querySelector('#booking-surname').value}</span>`;
    document.querySelector('#booking-email').outerHTML = `<span id='booking-email'>${document.querySelector('#booking-email').value}</span>`;
  })

  document.querySelector('#delete-booking').addEventListener('click', async function(){
    res = await deleteBooking(document.getElementById('booking-management-field').value);

    switch (res.status){
      case 204: 
        alert(`Booking deletion has been completed with success!`);
        break;
      case 404: 
        alert("Booking deletion failed due to booking-id not found!"); 
        location.reload();
        break;
      case 500: 
        alert("Booking deletion failed due to server error!");
        location.reload();
        break;
    }

    location.reload();
  })
});

//end document ready

function resetSeats(){
  document.querySelector('#seating-section').style.display = "none";
  document.querySelector('#insert-booking-informations').style.display = "none";
  loadedProjection = undefined;
  selectedSeats = [];
  document.querySelector("#projections-selection").value = -1;
  document.querySelector('#seating-section-modify').style.display = "none";
}

async function loadProjections(movieId){
  let projections = await getAllProjections(movieId);
  let select = document.getElementById('projections-selection');
  select.innerHTML = '';
  let defaultOption = new Option("Seleziona una proiezione", -1);
  select.append(defaultOption);
  projections.forEach(proj => {
      var newOption = new Option(proj.date + " " + proj.timetable, proj.id);
      select.append(newOption);
  });
}

async function showSeats (projId, oldBooking = undefined) {
  let modifying;

  if(oldBooking === undefined)
    modifying = false;
  else
    modifying = true;

  loadedProjection = projId;

  let projection = projections.find(p => p.id === projId).hall_id;

  let hall = await getHallById(projection);

  let occupiedSeats = await getBookedSeats(projId);

  columns = hall.columns;
  rows = hall.rows;

  let container;

  if(modifying) {
    let seatingSectionModify = document.querySelector('#seating-section-modify');
    if (seatingSectionModify !== null) {
        seatingSectionModify.style.display = "block";
    }

    let seatingLegendModify = document.querySelector('#seating-legend-modify')
    if(seatingLegendModify !== null){
      seatingLegendModify.style.display = "block";
    }

    selectedSeats = oldBooking.seats; //[{"row":4, "column": 4}, ...]
    container = document.querySelector('#seating-container-modify');
  } else {
    let seatingSection = document.querySelector('#seating-section');
    if(seatingSection !== null){
      seatingSection.style.display = "block";
    }

    let insertBookingInformations = document.querySelector('#insert-booking-informations');
    if(insertBookingInformations !== null){
      insertBookingInformations.style.display = "block";
    }

    selectedSeats = [];
    container = document.querySelector('#seating-container');
  }
  
  while (container.firstChild) {
    container.removeChild(container.firstChild);
  }

  const columnLabels = [...Array(columns).keys()].map(i => String.fromCharCode(i + 65));
  const rowLabels = [...Array(rows).keys()].map(i => i + 1);

  rowLabels.forEach(rowLabel => {
    columnLabels.forEach(columnLabel => {
      var occupied = false;
      var selected = false;
      if(occupiedSeats !== undefined){
        occupiedSeats.forEach(occupiedSeat => {
          if(String.fromCharCode(occupiedSeat.column + 64) === columnLabel && occupiedSeat.row === rowLabel){
            occupied = true;
          }
        })
      }
      if(selectedSeats !== undefined){
        selectedSeats.forEach(selectedSeat => {
          if(String.fromCharCode(selectedSeat.column + 64) === columnLabel && selectedSeat.row === rowLabel){
            selected = true;
          }
        })
      }

      const seatButton = document.createElement('button');
      seatButton.classList.add('seat');
      seatButton.innerText = columnLabel + rowLabel;

      if(occupied && !selected) {
        seatButton.classList.add('occupied');
      } else {
        seatButton.addEventListener('click', function(event) {
          event.preventDefault();
          this.classList.toggle('selected');

          let insertBooking = document.getElementById('insert-booking-informations');
          let submitChanges = document.getElementById('submit-changes');

          if (!modifying && document.querySelectorAll('#seating-container .seat.selected').length > 0) {
              insertBooking.disabled = false;
          } else {
              insertBooking.disabled = true;
          }

          if (modifying && document.querySelectorAll('#seating-container-modify .seat.selected').length > 0) {
              submitChanges.disabled = false;
          } else {
              submitChanges.disabled = true;
          }
        });

        if(selected){
            seatButton.classList.add('selected');
        }
      }
      container.append(seatButton);
    });
    container.append(document.createElement('br'));
  });
}

async function loadBooking(id){
    let booking = await getBookingById(id);
  
    let bookingDetails = document.getElementById('booking-details');
    let bookingNotFound = document.getElementById('booking-not-found');

    if(booking === "Not found") {
        bookingDetails.style.display = 'none';
        bookingNotFound.style.display = 'block';
    } else {
        bookingNotFound.style.display = 'none';

        // Mostra i dettagli della prenotazione
        document.getElementById('booking-name').textContent = booking.name;
        document.getElementById('booking-surname').textContent = booking.surname;
        document.getElementById('booking-email').textContent = booking.email;

        let projection = await getProjectionById(booking.proj_id);
        let movie = await getMovieById(projection.movie_id);

        document.getElementById('booking-hall').textContent = projection.hall_id;
        document.getElementById('booking-movie').textContent = movie.name;
        document.getElementById('booking-date').textContent = projection.date;
        document.getElementById('booking-timetable').textContent = projection.timetable;
    
        // Mostra i posti prenotati
        let bookingSeats = document.getElementById('booking-seats');
        bookingSeats.innerHTML = '';  // Rimuovo eventuali posti prenotati precedenti
        for (let seat of booking.seats) {
            let li = document.createElement('li');
            li.textContent = `Riga: ${seat.row}, Colonna: ${seat.column}`;
            bookingSeats.append(li);
        }
    
        // Mostra la sezione dei dettagli
        bookingDetails.style.display = 'block';
    }
}


//funzioni API ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

// Movies
async function getAllMovies() {
  res = await fetch(`${API_URL}/movies`);

  switch(res.status){
    case 200: return res.json();
    case 500: 
      alert("Booking failed due to server error!"); 
      location.reload();
      break;
  }
}

async function getMovieById(id) {
  res = await fetch(`${API_URL}/movies/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

/* function addMovie(movie) {
  return fetch(`${API_URL}/movies`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie)
  });
}

function updateMovie(id, movie) {
  return fetch(`${API_URL}/movies/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(movie)
  });
}

function deleteMovie(id) {
  return fetch(`${API_URL}/movies/${id}`, {
    method: "DELETE"
  });
}
 */

// Halls
async function getAllHalls() {
  res = await fetch(`${API_URL}/halls`);

  switch(res.status){
    case 200: return res.json();
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

async function getHallById(id) {
  res = await fetch(`${API_URL}/halls/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

/* function addHall(hall) {
  return fetch(`${API_URL}/halls`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(hall)
  });
}

function updateHall(id, hall) {
  return fetch(`${API_URL}/halls/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(hall)
  });
}

function deleteHall(id) {
  return fetch(`${API_URL}/halls/${id}`, {
    method: "DELETE"
  });
} */

// Projections
async function getAllProjections(movieId = "") {
  if(movieId === "")
    res = await fetch(`${API_URL}/projections`);
  else
    res = await fetch(`${API_URL}/projections?movie_id=${movieId}`);

  switch(res.status){
    case 200: return res.json();
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

async function getProjectionById(id) {
  res = await fetch(`${API_URL}/projections/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

/* function addProjection(projection) {
  return fetch(`${API_URL}/projections`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(projection)
  });
}

function updateProjection(id, projection) {
  return fetch(`${API_URL}/projections/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(projection)
  });
}

function deleteProjection(id) {
  return fetch(`${API_URL}/projections/${id}`, {
    method: "DELETE"
  });
} */

// Bookings
/* function getAllBookings(projId = 0) {
  return fetch(`${API_URL}/bookings?proj_id=${projId}`).then(response => response.json());
} */

async function getBookingById(id) {
  res = await fetch(`${API_URL}/bookings/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500:
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}

async function addBooking(booking) {
  return await fetch(`${API_URL}/bookings`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(booking)
  });
}

async function updateBooking(id, booking) {
  return await fetch(`${API_URL}/bookings/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(booking)
  });
}


async function deleteBooking(id) {
  return await fetch(`${API_URL}/bookings/${id}`, {
    method: "DELETE"
  });
}

// Seats
async function getBookedSeats(projId) {
  res = await fetch(`${API_URL}/projections/${projId}/seats`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: 
      alert("Booking failed due to server error!");
      location.reload();
      break;
  }
}