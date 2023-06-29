const API_URI = "http://localhost:8080";

//when the document is ready
$(document).ready(async () => {
  //fetch projections
  //let projections = await getAllProjections();

  $('#seating-section').hide();
  $('#submit-booking').hide();

  //to delete
  let projections = await (await fetch('./mocks testing/projections.json')).json();
  let movies = await(await fetch('./mocks testing/movies.json')).json();

  //show projections
  projections.filter((_, index) => index < 5 ).forEach(proj => {
    //const movie = getMovieById(proj.movie_id);
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

  //load bookings into drop-down
  loadBookings();

  
  $('#projections-selection').change(async function() {
    const selectedProjection = Number($(this).val());
    if(selectedProjection === -1){
      $('#seating-section').hide();
      $('#submit-booking').hide();
    } else {
      await showSeatsProj(selectedProjection);
    }
  });
})

async function loadBookings(){
  let bookings = await(await fetch('./mocks testing/bookings.json')).json();
  
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
  let projections = await(await fetch('./mocks testing/projections.json')).json();
  let projection = projections.find(p => p.id === projId);
  let seats = await(await fetch('./mocks testing/seats.json')).json();
  let halls = await(await fetch('./mocks testing/halls.json')).json();
  console.log(projection)
  let hall = halls.find(h => h.id === projection.hall_id);
  let occupiedSeats = seats.filter(s => s.proj_id === projId);
  await showSeats(hall.columns, hall.rows, occupiedSeats);
}

async function showSeats (columns, rows, occupiedSeats) {

  $('#seating-section').show();
  $('#submit-booking').show();
  
  const container = $('#seating-container');
  container.empty();

  const columnLabels = [...Array(columns).keys()].map(i => String.fromCharCode(i + 65));
  const rowLabels = [...Array(rows).keys()].map(i => i + 1);

  rowLabels.forEach(rowLabel => {
      columnLabels.forEach(columnLabel => {
          var occupied = false;
          occupiedSeats.forEach(occupiedSeat => {
            if(String.fromCharCode(occupiedSeat.column + 65) === columnLabel && occupiedSeat.row + 1 === rowLabel){
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
  return res.json();
}

async function getMovieById(id) {
  res = await fetch(`${API_URL}/movies/${id}`);
  return res.json();
}

function addMovie(movie) {
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

// Halls
function getAllHalls() {
  return fetch(`${API_URL}/halls`).then(response => response.json());
}

function getHallById(id) {
  return fetch(`${API_URL}/halls/${id}`).then(response => response.json());
}

function addHall(hall) {
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
}

// Projections
function getAllProjections(movieId = 0) {
  res = fetch(`${API_URL}/projections?movie_id=${movieId}`);
  return res.json();
}

function getProjectionById(id) {
  return fetch(`${API_URL}/projections/${id}`).then(response => response.json());
}

function addProjection(projection) {
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
}

// Bookings
function getAllBookings(projId = 0) {
  return fetch(`${API_URL}/bookings?proj_id=${projId}`).then(response => response.json());
}

function getBookingById(id) {
  return fetch(`${API_URL}/bookings/${id}`).then(response => response.json());
}

function addBooking(booking) {
  return fetch(`${API_URL}/bookings`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(booking)
  });
}

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
}

// Seats
function getBookedSeats(projId) {
  return fetch(`${API_URL}/projections/${projId}/seats`).then(response => response.json())
}
