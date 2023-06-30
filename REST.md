# Progetto Sistemi Distribuiti 2022-2023 - API REST

Documentazione delle API REST. I dati vengono scambiati in formato JSON.

## `/movies`

### GET

**Descrizione**: Restituisce l'elenco di tutti i film.

**Parametri**: Nessuno.

**Body richiesta**: Non previsto.

**Risposta**: Un array di oggetti, ognuno rappresentante un film disponibile. Ogni film ha i seguenti campi:
* `id` (un intero)
* `name` (una stringa)
* `description` (una stringa)
* `duration` (una stringa)
* `image` (una stringa)

**Codici di stato restituiti**:
* 200 OK
* 204 No Content

### POST

**Descrizione**: Aggiunge un nuovo film.

**Parametri**: L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta il nuovo film, con i seguenti campi:
* `name` (una stringa)
* `description` (una stringa)
* `duration` (una stringa)
* `image` (una stringa)

**Risposta**: In caso di successo, il body di ritorno è il film creato.

**Codici di stato restituiti**:
* 201 Created
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).

## `/movies/{movie_id}`

### GET

**Descrizione**: Restituisce i dettagli del film specificato.

**Parametri**: `movie_id` (un intero) nell'URL, che rappresenta l'ID del film.

**Body richiesta**: Non previsto.

**Risposta**: Un oggetto che rappresenta il film, con i seguenti campi:
* `id` (un intero)
* `name` (una stringa)
* `description` (una stringa)
* `duration` (una stringa)
* `image` (una stringa)

**Codici di stato restituiti**:
* 200 OK
* 404 Not Found: Film non trovato.

### PUT

**Descrizione**: Aggiorna un film esistente.

**Parametri**: `movie_id` (un intero) nell'URL, che rappresenta l'ID del film da aggiornare. L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta il film aggiornato, con i seguenti campi:
* `name` (una stringa)
* `description` (una stringa)
* `duration` (una stringa)
* `image` (una stringa)

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).
* 404 Not Found: Film non trovato.

### DELETE

**Descrizione**: Elimina un film.

**Parametri**: `movie_id` (un intero) nell'URL, che rappresenta l'ID del film da eliminare.

**Body richiesta**: Non previsto.

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 404 Not Found: Film non trovato.

## `/halls`

### GET

**Descrizione**: Restituisce l'elenco di tutte le sale.

**Parametri**: Nessuno.

**Body richiesta**: Non previsto.

**Risposta**: Un array di oggetti, ognuno rappresentante una sala. Ogni sala ha i seguenti campi:
* `id` (un intero)
* `rows` (un intero)
* `columns` (un intero)

**Codici di stato restituiti**:
* 200 OK
* 204 No Content

### POST

**Descrizione**: Aggiunge una nuova sala.

**Parametri**: L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la nuova sala, con i seguenti campi:
* `rows` (un intero)
* `columns` (un intero)

**Risposta**: In caso di successo, il body di ritorno avrà la sala creata.

**Codici di stato restituiti**:
* 201 Created
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).

## `/halls/{hall_id}`

### GET

**Descrizione**: Restituisce i dettagli della sala specificata.

**Parametri**: `hall_id` (un intero) nell'URL, che rappresenta l'ID della sala.

**Body richiesta**: Non previsto.

**Risposta**: Un oggetto che rappresenta la sala, con i seguenti campi:
* `id` (un intero)
* `rows` (un intero)
* `columns` (un intero)

**Codici di stato restituiti**:
* 200 OK
* 404 Not Found: Sala non trovata.

### PUT

**Descrizione**: Aggiorna una sala esistente.

**Parametri**: `hall_id` (un intero) nell'URL, che rappresenta l'ID della sala da aggiornare. L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la sala aggiornata, con i seguenti campi:
* `rows` (un intero)
* `columns` (un intero)

**Risposta**: In caso di successo, il body di ritorno è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).
* 404 Not Found: Sala non trovata.

### DELETE

**Descrizione**: Elimina una sala.

**Parametri**: `hall_id` (un intero) nell'URL, che rappresenta l'ID della sala da eliminare.

**Body richiesta**: Non previsto.

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 404 Not Found: Sala non trovata.

## `/projections`

### GET

**Descrizione**: Restituisce tutte le proiezioni. Le proiezioni già avvenute vengono scartate. Se viene fornito un `movie_id`, restituisce solo le proiezioni per quel film.

**Parametri**: `movie_id` (un intero) nella query string dell'URL, opzionale.

**Body richiesta**: Non previsto.

**Risposta**: Un array di oggetti, ognuno rappresentante una proiezione. Ogni proiezione ha i seguenti campi:
* `id` (un intero)
* `hall_id` (un intero)
* `movie_id` (un intero)
* `date` (una stringa nel formato "gg/mm/aaaa")
* `timetable` (una stringa nel formato "hh:mm")

**Codici di stato restituiti**:
* 200 OK
* 204 No Content

### POST

**Descrizione**: Inserisce una nuova proiezione.

**Parametri**: L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la nuova proiezione, con i seguenti campi:
* `hall_id` (un intero)
* `movie_id` (un intero)
* `date` (una stringa nel formato "gg/mm/aaaa")
* `timetable` (una stringa nel formato "hh:mm")

**Risposta**: In caso di successo, il body di ritorno è la proiezione creata.

**Codici di stato restituiti**:
* 201 Created
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).

## `/projections/{proj_id}`

### GET

**Descrizione**: Restituisce i dettagli della proiezione specificata.

**Parametri**: `proj_id` (un intero) nell'URL, che rappresenta l'ID della proiezione.

**Body richiesta**: Non previsto.

**Risposta**: Un oggetto che rappresenta la proiezione, con i seguenti campi:
* `id` (un intero)
* `hall_id` (un intero)
* `movie_id` (un intero)
* `date` (una stringa nel formato "gg/mm/aaaa")
* `timetable` (una stringa nel formato "hh:mm")

**Codici di stato restituiti**:
* 200 OK
* 404 Not Found: Proiezione non trovata.

### PUT

**Descrizione**: Aggiorna una proiezione esistente.

**Parametri**: `proj_id` (un intero) nell'URL, che rappresenta l'ID della proiezione da aggiornare. L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la proiezione aggiornata, con i seguenti campi:
* `hall_id` (un intero)
* `movie_id` (un intero)
* `date` (una stringa nel formato "gg/mm/aaaa")
* `timetable` (una stringa nel formato "hh:mm")

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).
* 404 Not Found: Proiezione, Sala o Film non trovato.

### DELETE

**Descrizione**: Elimina una proiezione.

**Parametri**: `proj_id` (un intero) nell'URL, che rappresenta l'ID della proiezione da eliminare.

**Body richiesta**: Non previsto.

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 404 Not Found: Proiezione non trovata.

## `/bookings`

### GET

**Descrizione**: Restituisce tutte le prenotazioni. Se viene fornito un `proj_id`, restituisce solo le prenotazioni per quella proiezione.

**Parametri**: `proj_id` (un intero) nell'URL, opzionale.

**Body richiesta**: Non previsto.

**Risposta**: Un array di oggetti, ognuno rappresentante una prenotazione. Ogni prenotazione ha i seguenti campi:
* `id` (un intero)
* `proj_id` (un intero)
* `row` (un intero)
* `column` (un intero)
* `name` (una stringa)
* `surname` (una stringa)
* `email` (una stringa)

**Codici di stato restituiti**:
* 200 OK
* 204 No Content

### POST

**Descrizione**: Inserisce una nuova prenotazione.

**Parametri**: L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la nuova prenotazione, con i seguenti campi:
* `proj_id` (un intero)
* `row` (un intero)
* `column` (un intero)
* `name` (una stringa)
* `surname` (una stringa)
* `email` (una stringa)

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 201 Created
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).
* 404 Not Found: la proiezione non esiste
* 409 Conflict: i posti sono già stati prenotati

## `/bookings/{book_id}`

### GET

**Descrizione**: Restituisce i dettagli della prenotazione specificata.

**Parametri**: `book_id` (un intero) nell'URL, che rappresenta l'ID della prenotazione.

**Body richiesta**: Non previsto.

**Risposta**: Un oggetto che rappresenta la prenotazione, con i seguenti campi:
* `id` (un intero)
* `proj_id` (un intero)
* `row` (un intero)
* `column` (un intero)
* `name` (una stringa)
* `surname` (una stringa)
* `email` (una stringa)

**Codici di stato restituiti**:
* 200 OK
* 404 Not Found: Prenotazione non trovata.

### PUT

**Descrizione**: Aggiorna una prenotazione esistente.

**Parametri**: `book_id` (un intero) nell'URL, che rappresenta l'ID della prenotazione da aggiornare. L'header `Content-Type: application/json`.

**Body richiesta**: Un oggetto JSON che rappresenta la prenotazione aggiornata, con i seguenti campi:
* `proj_id` (un intero)
* `row` (un intero)
* `column` (un intero)
* `name` (una stringa)
* `surname` (una stringa)
* `email` (una stringa)

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 400 Bad Request: Errore del client (JSON non valido, campo mancante o altro).
* 404 Not Found: Prenotazione non trovata.
* 409 Conflict: i posti sono già stati prenotati

### DELETE

**Descrizione**: Elimina una prenotazione.

**Parametri**: `book_id` (un intero) nell'URL, che rappresenta l'ID della prenotazione da eliminare.

**Body richiesta**: Non previsto.

**Risposta**: In caso di successo, il body è vuoto.

**Codici di stato restituiti**:
* 204 No Content
* 404 Not Found: Prenotazione non trovata.

## `/projections/{proj_id}/seats`

### GET

**Descrizione**: Restituisce tutti i posti prenotati per una data proiezione.

**Parametri**: `proj_id` (un intero) nell'URL, che rappresenta l'ID della proiezione.

**Body richiesta**: Non previsto.

**Risposta**: Un array di oggetti, ognuno rappresentante un posto prenotato. Ogni posto ha i seguenti campi:
* `proj_id` (un intero)
* `row` (un intero)
* `column` (un intero)

**Codici di stato restituiti**:
* 200 OK
* 404 Not Found: Proiezione non trovata.