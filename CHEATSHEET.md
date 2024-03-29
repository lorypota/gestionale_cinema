# API Cheatsheet

## Movies

Movie model

```json
{
    "id": 0,
    "name": "Io e il mio kouhai",
    "description": "Un film molto bello zio",
    "duration": "1h 30m",
    "image": "https://pad.mymovies.it/filmclub/2006/08/218/locandina.jpg"
}
```

| Description | Method | Endpoint | Request | Response |
| --- | --- | --- | --- | --- |
| Get all movies | GET | `/movies` | N/A | Movie[] |
| Get movie by id | GET | `/movies/{movie_id}` | movie_id: int | Movie |
| Insert new movie | POST | `/movies` | body: Movie | HTTP 201 |
| Update movie | PUT | `/movies/{movie_id}` | movie_id: int, body: Movie | HTTP 204 |
| Delete movie | DELETE | `/movies/{movie_id}` | movie_id: int | HTTP 204 |

## Halls

Hall model

```json
{
    "id": 0,
    "rows": 10,
    "columns": 5
}
```

| Description | Method | Endpoint | Request | Response |
| --- | --- | --- | --- | --- |
| Get all halls | GET | `/halls` | N/A | Hall[] |
| Get hall by id | GET | `/halls/{hall_id}` | hall_id: int | Hall |
| Insert new hall | POST | `/halls` | body: Hall | HTTP 201 |
| Update hall | PUT | `/halls/{hall_id}` | hall_id: int, body: Hall | HTTP 204 |
| Delete hall | DELETE | `/halls/{hall_id}` | hall_id: int | HTTP 204 |

## Projections

Projection model

```json
{
    "id": 0,
    "hall_id": 0,
    "movie_id": 0, 
    "date": "20/05/2023",
    "timetable": "11:30"
}
```

| Description | Method | Endpoint | Request | Response |
| --- | --- | --- | --- | --- |
| Get all projections | GET | `/projections?movie_id=0` | movie_id?: int (opzionale) | Projection[] |
| Get projection by id | GET | `/projections/{proj_id}` | proj_id: int | Projection |
| Insert new projection | POST | `/projections` | body: Projection | HTTP 201 |
| Update projection | PUT | `/projections/{proj_id}` | proj_id: int, body: Projection | HTTP 204 |
| Delete projection | DELETE | `/projections/{proj_id}` | proj_id: int | HTTP 204 |
| Get all booked seats | GET | `/projections/{proj_id}/seats` | proj_id: int | Seat[] |

## Bookings

Booking model

```json
{
    "id": 0,
    "proj_id": 0,
    "seats": [
        {
            "row": 3,
            "column": 4
        },
        {
            "row": 2,
            "column": 1
        }
    ],
    "name": "Lorenzo",
    "surname": "Karzal Pellegrini",
    "email": "damiano.rota@youness.ma"
}
```

| Description | Method | Endpoint | Request | Response |
| --- | --- | --- | --- | --- |
| Get all bookings | GET | `/bookings?proj_id=0` | proj_id?: int (opzionale) | Booking[] |
| Get booking by id | GET | `/bookings/{book_id}` | book_id: int | Booking |
| Insert new booking | POST | `/bookings` | body: Booking | HTTP 201 |
| Update booking | PUT | `/bookings/{book_id}` | book_id: int, body: Booking | HTTP 204 |
| Delete booking | DELETE | `/bookings/{book_id}` | book_id: int | HTTP 204 |
