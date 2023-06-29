const API_URL = "http://localhost:8080";

//global variables
var projections;
var movies;
var loadedProjection = undefined;
let selectedSeats = [];

//once the document is ready
$(document).ready(async () => {

  $('#seating-section').hide();
  $('#insert-booking-informations').hide();
  
  projections = await getAllProjections();
  movies = await getAllMovies();

  //show projections
  projections.filter((_, index) => index < 5 ).forEach(async proj => {
    const movie = movies.find(m => m.id === proj.movie_id);

    const projLabel = $('<button>').addClass('projLabel');
    const title = $('<div>').addClass('projTitle').text(movie.name);
    const dateTimetable = $('<div>').addClass('projDateTime');
    dateTimetable.append($('<div>').addClass('projDate').text(proj.date));
    dateTimetable.append($('<div>').addClass('projTimetable').text(proj.timetable));
    const duration = $('<div>').addClass('projDuration').text("Durata: " + movie.duration);

    projLabel.append(title, dateTimetable, duration);
    projLabel.css("background-image", `url('${movie.image}')`);
    projLabel.css("background-size", "cover");
    projLabel.click( async () => await showSeatsProj(proj.id));

    $('#projections-container').append(projLabel);
  });

  //load projections into drop-down
  loadProjections(projections, movies);
  
  $('#projections-selection').change(async function() {
    const selectedProjection = Number($(this).val());
    if(selectedProjection === -1){
      $('#seating-section').hide();
      $('#insert-booking-informations').hide();
      loadedProjection = undefined;
      selectedSeats = [];
    } else {
      await showSeatsProj(selectedProjection);
    }
  });

  $('#booking-management-field').change(async function() {
    const bookingId = Number($(this).val());
    await loadBooking(bookingId);
  });

  // Quando l'utente clicca sul bottone, apri il modal 
  $("#insert-booking-informations").click(function(event) {
    event.preventDefault();
    $("body").css("overflow", "hidden"); // Nasconde la barra di scorrimento

    $('#seating-container .seat.selected').each(function(){
      selectedSeats.push($(this).text());
    });

    $("#selected-seats").text(selectedSeats.join(', ')); 

    $("#booking-infos").show();
  });

  // Quando l'utente clicca su <span> (x), chiudi il modal
  $(".close").click(function() {
    $("body").css("overflow", "auto"); // Riporta la barra di scorrimento
    $("#booking-infos").hide();
  });

  // Quando l'utente clicca ovunque fuori dal modal, lo chiudi
  $(window).click(function(event) {
    if (event.target == $("#booking-infos")[0]) {
      $("body").css("overflow", "auto"); // Riporta la barra di scorrimento
      $("#booking-infos").hide();
    }
  });

  $('#submit-booking').click(async function(event){
    event.preventDefault();
    //controllo con fetch che i posti siano ancora disponibili
    if(loadedProjection === undefined)
      throw new Error('Non è stata selezionata alcuna proiezione');
    let bookedSeats = await getBookedSeats(loadedProjection);
    
    selectedSeatsTransformed = selectedSeats.map(seat => {
      const column = seat.charCodeAt(0) - 'A'.charCodeAt(0) + 1;
      const row = seat.slice(1);
      return { "row": Number(row), "column": Number(column) };
    })
    
    for(let i = 0; i < bookedSeats.length; i++) {
      for(let j = 0; j < selectedSeatsTransformed.length; j++) {
        if(bookedSeats[i].row === selectedSeatsTransformed[j].row && 
           bookedSeats[i].column === selectedSeatsTransformed[j].column) {
            //dico all'utente che i posti non sono più disponibili e ricarico la pagina
            //DA TESTARE ATTENZIONE AAAA
        }
      }
    }

    //se disponibili confermo prenotazione e mostro dati prenotazione
    let newBooking = {
      "proj_id": loadedProjection,
      "seats": selectedSeatsTransformed,
      "name": $('#fname').val(),
      "surname": $('#lname').val(),
      "email": $('#email').val()
    }
    
    response = await addBooking(newBooking);

    switch (response.status) {
      case 201: 
        alert(`Booking has been completed with success! Your booking id is: ${response.json().id}`);
        break;
      case 400: alert("Booking failed due to missing fields!"); break;
      case 500: alert("Booking failed due to server error!"); break;
    }

    location.reload();
  });

});

async function loadBooking(id){
  let booking = await getBookingById(id);
  console.log(booking)
}

async function loadProjections(projections, movies){
  projections.forEach(proj => {
    const movie = movies.find(m => m.id === proj.movie_id);
    
    var newOption = document.createElement('option');
    newOption.innerText = movie.name;
    newOption.value = proj.id;

    $('#projections-selection').append(newOption);
  });
}

async function showSeatsProj (projId){
  //salvo projId in una variabile globale per gestirla successivamente nel modal
  loadedProjection = projId;
  let projection = projections.find(p => p.id === projId);
  let halls = await(await fetch('./mocks testing/halls.json')).json();
  let hall = halls.find(h => h.id === projection.hall_id);
  let occupiedSeats = await getBookedSeats(projId);
  /* console.log(occupiedSeats) */
  await showSeats(hall.columns, hall.rows, occupiedSeats);
}

async function showSeats (columns, rows, occupiedSeats) {

  $('#seating-section').show();
  $('#insert-booking-informations').show();
  
  selectedSeats = [];
  const container = $('#seating-container');
  container.empty();

  const columnLabels = [...Array(columns).keys()].map(i => String.fromCharCode(i + 65));
  const rowLabels = [...Array(rows).keys()].map(i => i + 1);

  rowLabels.forEach(rowLabel => {
      columnLabels.forEach(columnLabel => {
          var occupied = false;
          occupiedSeats.forEach(occupiedSeat => {
            /* console.log("col:" + columnLabel + " row:" + rowLabel)
            console.log("col:" + String.fromCharCode(occupiedSeat.column + 64) + " row: " + (occupiedSeat.row) */
            if(String.fromCharCode(occupiedSeat.column + 64) === columnLabel && occupiedSeat.row === rowLabel){
              occupied = true;
            }
          })

          const seatButton = $('<button>').addClass('seat').text(columnLabel + rowLabel);
          if(occupied) {
            seatButton.addClass('occupied');
          } else {
            seatButton.click( function(event) {
              event.preventDefault();
              $(this).toggleClass('selected');

              if ($('#seating-container .seat.selected').length > 0) {
                $('#insert-booking-informations').prop('disabled', false);
              } else {
                $('#insert-booking-informations').prop('disabled', true);
              }
            });
          }
          container.append(seatButton);
      });
      container.append('<br>');
  });

}

//APIs functions ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

// Movies
async function getAllMovies() {
  res = await fetch(`${API_URL}/movies`);

  switch(res.status){
    case 200: return res.json();
    case 500: return "Server error";
  }
}

async function getMovieById(id) {
  res = await fetch(`${API_URL}/movies/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: return "Server error";
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
function getAllHalls() {
  return fetch(`${API_URL}/halls`).then(response => response.json());
}

function getHallById(id) {
  return fetch(`${API_URL}/halls/${id}`).then(response => response.json());
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
    case 500: return "Server error";
  }
}

async function getProjectionById(id) {
  res = await fetch(`${API_URL}/projections/${id}`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: return "Server error";
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
    case 500: return "Server error";
  }
}

async function addBooking(booking) {
  return fetch(`${API_URL}/bookings`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(booking)
  });
}

/*
function updateBooking(id, booking) {
  return fetch(`${API_URL}/bookings/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(booking)
  });
}

function deleteBooking(id) {
  return fetch(`${API_URL}/bookings/${id}`, {
    method: "DELETE"
  });
} */

// Seats
async function getBookedSeats(projId) {
  res = await fetch(`${API_URL}/projections/${projId}/seats`);

  switch(res.status){
    case 200: return res.json();
    case 404: return "Not found";
    case 500: return "Server error";
  }
}
