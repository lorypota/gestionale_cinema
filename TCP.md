# Progetto Sistemi Distribuiti 2022-2023 - TCP

Documentazione del protoccolo socket su TCP. Si suggerisce di seguire il protocollo di Redis (<https://redis.io/docs/reference/protocol-spec/>), perché è molto semplice sia da comprendere sia da implementare. Non è necessario prendere tutti i punti, basta quello necessario per l'invio della richiesta e della risposta.

La documentazione può variare molto in base al tipo di protocollo che si vuole costruire. Se come Redis, è un protocollo di richieste e risposte, per cui è necessario indicare come inviare la richiesta (comando e dati) e la risposta. Si può anche utilizzare JSON al posto delle semplici stringhe, in tal caso andranno specificati bene la struttura degli oggetti scambiati tra client e server.

Our protocol makes full use of Redis' RESP protocol, it supports pipelinening as well as bulk operations.

## RESP protocol description

The RESP protocol was introduced in Redis 1.2, but it became the standard way for talking with the Redis server in Redis 2.0. This is the protocol you should implement in your Redis client.

RESP is actually a serialization protocol that supports the following data types: Simple Strings, Errors, Integers, Bulk Strings, and Arrays.

Redis uses RESP as a request-response protocol in the following way:

Clients send commands to a Redis server as a RESP Array of Bulk Strings.
The server replies with one of the RESP types according to the command implementation.
In RESP, the first byte determines the data type:

For Simple Strings, the first byte of the reply is "+"
For Errors, the first byte of the reply is "-"
For Integers, the first byte of the reply is ":"
For Bulk Strings, the first byte of the reply is "$"
For Arrays, the first byte of the reply is "*"
RESP can represent a Null value using a special variation of Bulk Strings or Array as specified later.

In RESP, different parts of the protocol are always terminated with "\r\n" (CRLF).

Comandi HashMap:
- HDEL
- HGET
- HGETALL
- HLEN
- HKEYS
- HVALS
- HSET

Comandi String:
- GET
- SET
- LEN

Comandi generici:
- STRINGS
- HASHES
- PING
