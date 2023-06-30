const API_URL = "http://localhost:8080";

//variabili globali
var projections;
var movies;
var loadedProjection = undefined;
var selectedSeats = [];

//azioni su caricamento del documento
$(document).ready(async () => {

  $('#booking-section').hide();
  $('#seating-section').hide();
  $('#insert-booking-informations').hide();
  $('#seating-section-modify').hide();
  
  projections = await getAllProjections();
  movies = await getAllMovies();

  //mostra protiezioni
  movies.forEach(async movie => {
    const projLabel = $('<button>').addClass('projLabel');
    const title = $('<div>').addClass('projTitle').text(movie.name);
    const duration = $('<div>').addClass('projDuration').text("Durata: " + movie.duration);

    projLabel.append(title, duration);
    projLabel.css("background-image", `url('${movie.image}')`);
    projLabel.css("background-size", "cover");
    projLabel.click( async () => {
      $('#booking-section').show();
      resetSeats();
      loadProjections(movie.id);
    });

    $('#projections-container').append(projLabel);
  });
  
  $('#projections-selection').change(async function() {
    const selectedProjection = Number($(this).val());
    if(selectedProjection === -1){
      resetSeats();
    } else {
      await showSeats(selectedProjection);
    }
  });

  $('#booking-management-field').keydown(function(event){
    if (event.keyCode === 13) {  // 13 è il keyCode per il tasto Invio
        event.preventDefault();
    }
  });

  $('#booking-management-field').change(async function(event){
    const bookingId = Number($(this).val());
    const booking = await getBookingById(bookingId);

    if(projections.some(proj => proj.id === booking.proj_id)){
      $('#update-booking').show();
    } else {
      $('#update-booking').hide();
    }

    await loadBooking(bookingId);
  });

  // Quando l'utente clicca sul bottone, apro il modal 
  $("#insert-booking-informations").click(function(event) {
    event.preventDefault();
    $("body").css("overflow", "hidden"); // Nasconde la barra di scorrimento

    $('#seating-container .seat.selected').each(function(){
      selectedSeats.push($(this).text());
    });

    $("#selected-seats").text(selectedSeats.join(', ')); 

    $("#booking-infos").show();
  });

  // Quando l'utente clicca su <span> (x), chiudo il modal
  $(".close").click(function() {
    closeModal();
  });

  // Quando l'utente clicca ovunque fuori dal modal, lo chiudo
  $(window).click(function(event) {
    if (event.target == $("#booking-infos")[0]) {
      closeModal();
    }
  });

  function closeModal(){
    $("body").css("overflow", "auto"); // Mostra la barra di scorrimento
    $("#booking-infos").hide();
  }

  $('#submit-booking').click(async function(event){
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
      "name": $('#fname').val(),
      "surname": $('#lname').val(),
      "email": $('#email').val()
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
    $('#booking-section').hide();

    $("#booking-management-field").val(res.id);
    loadBooking(res.id);
  });

  $('#update-booking').click(async function(){
    let oldBooking = await getBookingById($('#booking-management-field').val());
    
    $('#update-booking').hide();
    $('#booking-seats').hide();
    $('#submit-changes').show();
    $('#seating-section-modify').show();

    $('#booking-name').replaceWith($("<input type='text' id='booking-name'>").val($('#booking-name').text()));
    $('#booking-surname').replaceWith($("<input type='text' id='booking-surname'>").val($('#booking-surname').text()));
    $('#booking-email').replaceWith($("<input type='text' id='booking-email'>").val($('#booking-email').text()));

    showSeats(oldBooking.proj_id, oldBooking);
  })

  $('#submit-changes').click(async function(event){
    event.preventDefault();

    if(loadedProjection === undefined)
      throw new Error('Non è stata selezionata alcuna proiezione');

    selectedSeats = [];
    
    $('#seating-container-modify .seat.selected').each(function () {
        selectedSeats.push($(this).text());
    });

    selectedSeatsTransformed = selectedSeats.map(seat => {
      const column = seat.charCodeAt(0) - 'A'.charCodeAt(0) + 1;
      const row = seat.slice(1);
      return { "row": Number(row), "column": Number(column) };
    })
    
    let oldBooking = await getBookingById($('#booking-management-field').val());
    let idBooking = oldBooking.id;

    delete oldBooking.id;

    let newBooking = {
      "proj_id": loadedProjection,
      "seats": selectedSeatsTransformed,
      "name": $('#booking-name').val(),
      "surname": $('#booking-surname').val(),
      "email": $('#booking-email').val()
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

    $("#booking-management-field").val(idBooking);
    loadBooking(idBooking);

    $('#update-booking').show();
    $('#booking-seats').show();
    $('#submit-changes').hide();
    $('#seating-section-modify').hide();

    $('#booking-name').replaceWith($("<span id='booking-name'>").val($('#booking-name').text()));
    $('#booking-surname').replaceWith($("<span id='booking-surname'>").val($('#booking-surname').text()));
    $('#booking-email').replaceWith($("<span id='booking-email'>").val($('#booking-email').text()));
  })

  $('#delete-booking').click(async function(){
    res = await deleteBooking($('#booking-management-field').val());

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
  $('#seating-section').hide();
  $('#insert-booking-informations').hide();
  loadedProjection = undefined;
  selectedSeats = [];
  $("#projections-selection").val(-1);
  $('#seating-section-modify').hide();
}

async function loadProjections(movieId){
  let projections = await getAllProjections(movieId);
  $('#projections-selection').empty();
  $('#projections-selection').append(new Option("Seleziona una proiezione", -1));
  projections.forEach(proj => {
    var newOption = document.createElement('option');
    newOption.innerText = proj.date + " " + proj.timetable;
    newOption.value = proj.id;

    $('#projections-selection').append(newOption);
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
    $('#seating-section-modify').show();
    $('#seating-legend-modify').show();
    selectedSeats = oldBooking.seats; //[{"row":4, "column": 4}, ...]
    container = $('#seating-container-modify');
  } else {
    $('#seating-section').show();
    $('#insert-booking-informations').show();
    selectedSeats = [];
    container = $('#seating-container');
  }
  
  container.empty();

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

        const seatButton = $('<button>').addClass('seat').text(columnLabel + rowLabel);
        if(occupied && !selected) {
          seatButton.addClass('occupied');
        } else {
          seatButton.click( function(event) {
            event.preventDefault();
            $(this).toggleClass('selected');
            
            if (!modifying && $('#seating-container .seat.selected').length > 0) {
              $('#insert-booking-informations').prop('disabled', false);
            } else {
              $('#insert-booking-informations').prop('disabled', true);
            }

            if (modifying && $('#seating-container-modify .seat.selected').length > 0) {
              $('#submit-changes').prop('disabled', false);
            } else {
              $('#submit-changes').prop('disabled', true);
            }
          });

          if(selected){
            seatButton.addClass('selected');
          }
        }
        container.append(seatButton);
    });
    container.append('<br>');
  });
}

async function loadBooking(id){
  let booking = await getBookingById(id);
  
  if(booking === "Not found") {
    $('#booking-details').hide();
    $('#booking-not-found').show();
  } else {
    $('#booking-not-found').hide();

    // Mostra i dettagli della prenotazione
    $('#booking-name').text(booking.name);
    $('#booking-surname').text(booking.surname);
    $('#booking-email').text(booking.email);

    let projection = await getProjectionById(booking.proj_id);
    let movie = await getMovieById(projection.movie_id);

    $('#booking-hall').text(projection.hall_id);
    $('#booking-movie').text(movie.name);
    $('#booking-date').text(projection.date);
    $('#booking-timetable').text(projection.timetable);
    
    // Mostra i posti prenotati
    $('#booking-seats').empty();  // Rimuove eventuali posti prenotati precedenti
    for (let seat of booking.seats) {
      $('#booking-seats').append(`<li>Riga: ${seat.row}, Colonna: ${seat.column}</li>`);
    }
  
    // Mostra la sezione dei dettagli
    $('#booking-details').show();
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