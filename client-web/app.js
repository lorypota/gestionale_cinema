const API_URI = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', () => showSeats(15, 7));

async function showSeats (columns, rows) {
  const container = document.getElementById('seating-container');

  const columnLabels = [...Array(columns).keys()].map(i => String.fromCharCode(i + 65));
  const rowLabels = [...Array(rows).keys()].map(i => i + 1);

  rowLabels.forEach(rowLabel => {
      columnLabels.forEach(columnLabel => {
          const seatButton = document.createElement('button');
          seatButton.classList.add('seat');
          seatButton.textContent = `${columnLabel}${rowLabel}`;
          seatButton.addEventListener('click', function() {
              this.classList.toggle('selected');
          });
          container.appendChild(seatButton);
      });
      container.appendChild(document.createElement('br'));
  });
}


//APIs functions ----------------------------------------------------------------------------------------------------------------------------------------------------------------------

// Movies
function getAllMovies() {
  return fetch(`${API_URL}/movies`).then(response => response.json());
}

function getMovieById(id) {
  return fetch(`${API_URL}/movies/${id}`).then(response => response.json());
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
  return fetch(`${API_URL}/projections?movie_id=${movieId}`).then(response => response.json());
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
