# Progetto Sistemi Distribuiti 2022-2023 - TCP

## Preface

Il protocollo che abbiamo deciso di implementare è lo stesso che usa Redis, data la sua semplicità e della specifica così dettagliata.

## Descrizione protocollo RESP

RESP è un protocollo di serializzazione richiesta-risposta che supporta i seguenti tipi: Stringhe semplici, Errori, Interi, Stringhe bulk (o binary-safe), e Array.

Il flusso di richiesta-risposta funziona in questo modo.

Per i comandi parametrizzati, il Client invia i comandi al server come un Array RESP di Stringhe bulk.
Il Server risponde con un tipo RESP a seconda dell'implementazione del comando.
Per i comandi senza parametri, si può inoltre inviare il comando come singola Stringa semplice.

In RESP, il primo byte determina il tipo del dato:

- Per Stringhe semplici, il primo byte della risposta è "+"
- Per Errori, il primo byte della risposta è "-"
- Per Interi, il primo byte della risposta è ":"
- Per Stringhe bulk, il primo byte della risposta è "$"
- Per Array, il primo byte della risposta è "*"
- RESP può rappresentare un valore Nullo tramite una speciale variazione di una Stringa bulk o di un Array come specificato più avanti.

In RESP, parti differenti del protocollo terminano sempre con "\r\n" (CRLF).

### Stringhe semplici RESP

Le Stringhe semplici sono codificate come segue: il carattere più, seguito da una stringa che non può contenere un carattere CR o LF (i newlines non sono ammessi), e terminate da CRLF (ovvero "\r\n").

Le Stringhe semplici sono usate per trasmettere stringhe non bynary-safe con un overhead minimo. Per esempio, alcuni comandi rispondono solo con "OK" se hanno avuto successo. Questa String semplice è codificata con i 5 byte seguenti:

```plaintext
"+OK\r\n"
```

Per inviare stringhe binary-safe si fa utilizzo si Stringhe bulk.

Quando il Server risponde con una stringa semplice, una libreria Client dovrebbe rispondere con una stringa composta dal primo carattere dopo il '+' fino alla fine della stringa, escludendo i byte per CRLF.

### Errori RESP

RESP ha un tipo dati specifico per gli errori. Sono simili a Stringhe RESP semplici, ma il primo carattere è un meno '-' invece che un più. La vera differenza tra i due è che il Client tratta gli errori come eccezioni, e la stringa che compone l'errore è il messaggio di errore stesso.

Il formato base è:

```plaintext
"-Error message\r\n"
```

Le risposte di Errore sono inviate solo quando qualcosa va storto, per esempio se provi a fare un operazione su un tipo di dati errato, o se il comando non esiste. Il Client deve quindi lancia un eccezione quando riceve una riposta di Errore.

### Interi RESP

Questo tipo è composta semplicemente da una stringa terminata da CRLF che rappresenta un intero, prefissata da un byte ":". Per esempio, `":0\r\n"` e `":1000\r\n"` sono risposte intere.

Non ci sono significati speciali da un ritorno intero. Tuttavia, l'intero ritornato è garantito essere nel range di un intero a 32-bit con segno.

Le risposte intere sono utilizzate inoltre per rappresentare valori booleani. Per esempio, comandi come `EXISTS` ritornano `1` per `true` e `0` per `false`.

Altri comandi potrebbero ritornare `1` se l'operazione è stata effettivamente eseguita e `0` altrimenti.

I seguenti comandi risponderrano con un intero: `DEL`, `EXISTS`, `INCR`, `DECR`, `STRLEN`, `HSTRLEN`.

### Stringhe bulk RESP

Le Stringhe bulk sono usate in modo da rappresentare un singola stringa binary-safe fino a 512 MB di lunghezza.

Bulk Strings are encoded in the following way:

1. Un byte "$" seguito dal numero di byte che compongono la stringa (lunghezza prefissata), terminata da CRLF.
2. I byte effetivi che compongono la stringa.
3. Un CRLF finale.

Per cui la stringa "ciao" è codificata come segue:

```plaintext
"$4\r\nciao\r\n"
```

Una stringa vuota è codificata con:

```plaintext
"$0\r\n\r\n"
```

Le Stringhe bulk possono inoltre essere usate in modo da segnalare la non-esistenza di un valore utilizzando un formato speciale per rappresentare un valore Nullo. In questo formato, la lunghezza è -1, e non è presente alcun dato. Null è rappresentato come:

```plaintext
"$-1\r\n"
```

Quest'ultima è chiamata Stringa bulk nulla.

L'API di una libreria Client non deve tornare una stringa vuota, ma un valore nullo, quando il server risponde con una Stringa bulk nulla.

### Array RESP

Il Client invia comandi al Server tramite Array RESP. A loro volta, alcuni comandi del Server, che ritornano collezioni di elementi al client, fanno uso di un Array come risposta. Come ad esempio il comando `HKEYS` che ritorna tutti i campi presenti in un hash.

Gli Array sono inviati con il formato seguente:

1. Un carattere '*' come primo byte, seguito dal numero degli elementi nell'array come numero intero, seguito da CRLF.
2. Un tipo RESP aggiuntivo per ogni elemento dell'array.

Perció un array vuoto è codificato come segue:

```plaintext
"*0\r\n"
```

Mentre un array che codifica 2 Stringhe bulk "ciao" e "mondo" è coficato come:

```plaintext
"*2\r\n$4\r\nciao\r\n$5\r\nmondo\r\n"
```

Come si può notare dopo la parte di prefisso dell'Array `*<count>CRLF`, gli altri tipi di dato che compongono l'array sono concatenati uno dopo l'altro. Per esempio, un Array di 3 interi è codificato come segue:

```plaintext
"*3\r\n:1\r\n:2\r\n:3\r\n"
```

Gli array possono contenere tipi misti, non è quindi necessario che gli elementi siano dello stesso tipo. Per esempio, una lista con 4 interi e una stringa bulk si codifica come:

```plaintext
*5\r\n
:1\r\n
:2\r\n
:3\r\n
:4\r\n
$5\r\n
hello\r\n
```

*(La risposta è stata separata in più righe per chiarezza).*

La prima linea che il server invia è `*5\r\n` in modo da specificare che 5 risposte la seguiranno. Poi ogni risposta che rappresenta gli oggetti della risposta Multi-Bulk sono trasmesse.

Gli Array null vengono convertiti a Stringhe bulk nulle

```plaintext
"*-1\r\n"
```

*(Il Server la tratta come `$-1\r\n`).*

Array innestati sono possibili in RESP. Per esempio un array innestato con due array si codifica come:

```plaintext
*2\r\n
*3\r\n
:1\r\n
:2\r\n
:3\r\n
*2\r\n
+Ciao\r\n
-Mondo\r\n
```

*(La codifica è stata separata in più linee per facilitare la comprensione).*

Il tipo dati precedente codifica 2 Array che consistono in: 1 Array di 3 interi e un Array con una stringa semplice e un errore.

### Elemeti null negli Array

Gli elementi di un array possono essere anche Nulli. Questo per segnalare che questi elementi sono mancanti e non stringhe vuote. Un esempio di come è coficiato un Array con elementi nulli è:

```plaintext
*3\r\n
$4\r\n
ciao\r\n
$-1\r\n
$5\r\n
mondo\r\n
```

Il secondo elemento è un Null. La libreria Client dovrebbe ritornare qualcosa del tipo:

```json
["ciao",null,"mondo"]
```

Questa tuttavia non rappresenta un eccesione rispetto a ciò che è stato detto nelle sezioni precedenti, ma un esempio per spiegare nel dettaglio questo funzionamento del protocollo.

## Comandi

***Sintassi ed esempi si basano sull'input/output del tool `redis-cli`, encoding RESP adeguato dovrebbe essere usato.***
***Syntax and examples are based on `redis-cli` input/output, proper RESP encoding should be used.***

### Comandi utili

#### **COMMAND**

<h5>Sintassi</h5>

```plaintext
COMMAND [command:string]
```

Ritorna un array con dettagli riguardo ogni comando Server.

L'array di dettaglio è di un numero prefissato di elementi.

- Nome --> Nome del comando.
- Arità --> Arità è il numero di argomenti che il comando si aspetta. Segue un pattern semplice:
  - Un intero positivo significa un numero fisso di argomenti.
  - Un intero negativo significa un numero minimo di argomenti.

  L'arità dei comandi include sempre il nome del comando stesso.
- Flags --> N/A
- Prima chiave --> La posizione del primo argomento. Per la maggior parte dei comandi, la posizione della prima chiave è 1. La posizione 0 rappresenta sempre il nome del comando in sè.
- Ultima chiave --> La posizione dell'ultimo argomento.
- Step --> Lo step, o incremento, tra la posizione del primo argomento e quella dopo.

<h5> Ritorno </h5>

Risposta RESP Array: una lista di dettagli di comandi.

L'ordine è randomico.

<h5> Esempio </h5>

Il seguente outpute di COMMAND per il comando GET:

```plaintext
1) "get"
2) (integer) 2
3) (empty array)
4) (integer) 1
5) (integer) 1
6) (integer) 1
```

#### **PING**

<h5>Sintassi</h5>

```plaintext
PING
```

Ritorna PONG.
Questo comando è utile per:

1. Testare se una connessione è ancora in vita.
2. Misurare la latenza.

<h5> Ritorno </h5>

Risposta RESP Stringa semplica: PONG

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: PING
S: PONG
```

#### **STRINGS**

<h5> Sintassi </h5>

```plaintext
STRINGS
```

Ritorna un array con tutte le chiavi di tipo Stringa o Intero presenti nel database.

<h5> Ritorno </h5>

Risposta RESP Array: Un array contenenete le chiavi in tipo Stringa di tutti i valori di tipo Stringa o Intero presenti nel database.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: STRINGS
S: 1) "key1"
   2) "key2"
```

#### **HASHES**

<h5> Sintassi </h5>

```plaintext
HASHES
```

Ritorna un array con tutte le chiavi di tipo Hash presenti nel database.

<h5> Ritorno </h5>

Risposta RESP Array: Un array contenenete le chiavi di tipo Hash presenti nel database.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HASHES
S: 1) "hkey1"
   2) "hkey2"
```

### Comandi Strings

#### **SET**

<h5> Sintassi </h5>

```plaintext
SET <key:string> <value:string|number>
```

Setta la chiave `key` con il valore `value`. Se la chiave esiste già, il valore è sovrascritto, indipendentemente dal tipo di dati precedentemente memorizzato. Null è ritornato se la chiave memorizza un valore del tipo sbagliato. OK è ritornato se non aveva un valore precedente.

<h5> Ritorno </h5>

Risposta RESP Stringa semplice: OK se la chiave non aveva valori precedenti.
Risposta RESP Null: Se la chiave memorizza un valore del tipo sbagliato.
Risposta RESP Stringa Bulk: Il valore precedentemente memorizzato, se presente.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: SET key value
S: OK
C: SET key 10
S: "value"
C: SET key 11
S: (integer) 10
```

#### **GET**

<h5> Sintassi </h5>

```plaintext
GET <key:string>
```

Ritorna il valore memorizzato in `key`. Se la chiave non esiste, è ritornato un Null.

<h5> Ritorno </h5>

Risposta RESP Stringa Bulk o Intera: Il valore memorizzato in `key`, se la chiave esiste.
Risposta RESP Null: Se la chiave non esiste.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: SET key value
S: OK
C: GET key
S: "value"
C: GET ciao
S: (nil)
```

#### **DEL**

<h5> Sintassi </h5>

```plaintext
DEL <key:string>
```

Elimina la chiave `key` e ritorna 1. Altrimenti, 0 è ritornato.

<h5> Ritorno </h5>

Risposta RESP Intera: 1 se la chiave è stata eliminata, 0 altrimenti. (booleano)

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: SET key value
S: OK
C: DEL key
S: (integer) 1
C: DEL ciao
S: (integer) 0
```

#### **STRLEN**

<h5> Sintassi </h5>

```plaintext
STRLEN <key:string>
```

Ritorna la lunghezza della stringa memorizzata in `key`. Se la chiave non esiste, 0 è ritornato. Se la chiave memorizza un valore del tipo sbagliato, un errore è ritornato.

<h5> Ritorno </h5>

Risposta RESP Intera: La lunghezza della stringa memorizzata in `key`, 0 se la chiave non esiste.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: SET key value
S: OK
C: STRLEN key
S: (integer) 5
```

### Comandi Integers

#### **INCR**

<h5> Sintassi </h5>

```plaintext
INCR <key:string>
```

Incrementa il numero memorizzato in `key` di uno. Se la `key` non esiste, è settata a `0` prima di effetuare l'operazione. Un errore è ritornato se `key` memorizza un valore del tipo sbagliato. Questa operazione è limitata a interi a 32-bit con segno.

<h5> Ritorno </h5>

Risposta RESP intera: Il valore della chiave incrementato

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: INCR key
S: (integer) 1
C: INCR key
S: (integer) 2
```

#### **DECR**

<h5> Sintassi </h5>

```plaintext
DECR <key:string>
```

Decrementa il numero memorizzato in `key` di uno. Se la `key` non esiste, è settata a `0` prima di effetuare l'operazione. Un errore è ritornato se `key` memorizza un valore del tipo sbagliato. Questa operazione è limitata a interi a 32-bit con segno.

<h5> Ritorno </h5>

Risposta RESP intera: Il valore della chiave decrementato

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: DECR key
S: (integer) -1
C: DECR key
S: (integer) -2
```

### Comandi Hashes

#### **HDEL**

<h5> Sintassi </h5>

```plaintext
HDEL <key:string> <field:string>
```

Rimuove il campo `field` specificato dalla hashmap identificata dalla chiave `key`. Se la chiave non esiste o il campo non esiste nella hashmap, viene ritornato 0. Se il campo viene rimosso con successo, viene ritornato 1.

<h5> Ritorno </h5>

Risposta RESP Intera: 1 se il campo viene rimosso con successo dalla hashmap, 0 se la chiave non esiste o il campo non esiste nella hashmap.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HDEL myhash field1
S: (integer) 1
C: HDEL myhash field2
S: (integer) 0
C: HDEL anotherhash field1
S: (integer) 0
```

#### **HEXISTS**

<h5> Sintassi </h5>

```plaintext
HEXISTS <key:string> <field:string>
```

Verifica l'esistenza del campo `field` specificato nella hashmap identificata dalla chiave `key`. Se la chiave non esiste o la hashmap non esiste, viene ritornato 0. Se il campo esiste nella hashmap, viene ritornato 1.

<h5> Ritorno </h5>

Risposta RESP Intera: 1 se il campo esiste nella hashmap, 0 se la chiave non esiste o il campo non esiste nella hashmap.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HEXISTS myhash field1
S: (integer) 1
C: HEXISTS myhash field2
S: (integer) 0
C: HEXISTS anotherhash field1
S: (integer) 0
```

#### **HGETALL**

<h5> Sintassi </h5>

```plaintext
HGETALL <key:string>
```

Restituisce tutti i campi e i loro valori dalla hashmap identificata dalla chiave `key`. Se la chiave non esiste o la hashmap non esiste, viene restituito un array vuoto.

<h5> Ritorno </h5>

Risposta RESP Array: Un array di stringhe rappresentanti campi e valori dalla hashmap. Ogni nome del campo è seguito dal suo valore. Se la chiave non esiste o la hashmap non esiste, viene ritornato un array vuoto.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HGETALL myhash
S: 1) "field1"
   2) "value1"
   3) "field2"
   4) "value2"
C: HGETALL anotherhash
S: (empty array)
```

#### **HGET**

<h5> Sintassi </h5>

```plaintext
HGET <key:string> <field:string>
```

Restituisce il valore del campo `field` specificato nella hashmap identificata dalla chiave `key`. Se la chiave non esiste, la hashmap non esiste o il campo non esiste nella hashmap, viene restituito un valore null.

<h5> Ritorno </h5>

Risposta RESP Bulk String o RESP Number: Il valore del campo specificato nella hashmap. Se il campo non esiste, ritorna una stringa bulk null.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HGET myhash field1
S: "value1"
C: HGET myhash field2
S: (nil)
C: HGET anotherhash field1
S: (nil)
```

#### **HKEYS**

<h5> Sintassi </h5>

```plaintext
HKEYS <key:string>
```

Restituisce tutte le chiavi (campi) della hashmap identificata dalla chiave `key`. Se la chiave non esiste o la hashmap non esiste, viene restituito un array vuoto.

<h5> Ritorno </h5>

Risposta RESP Array: Un array di stringhe rappresentanti le chiavi della hashmap. Se la chiave non esiste o la hashmap non esiste, viene ritornato un array vuoto.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HKEYS myhash
S: 1) "field1"
   2) "field2"
C: HKEYS anotherhash
S: (empty array)
```

#### **HLEN**

<h5> Sintassi </h5>

```plaintext
HLEN <key:string>
```

Restituisce la lunghezza (numero di campi) della hashmap identificata dalla chiave `key`. Se la chiave non esiste o la hashmap non esiste, viene restituito 0.

<h5> Ritorno </h5>

Risposta RESP Numero: Il numero di campi presenti nella hashmap. Se la chiave non esiste o la hashmap non esiste, viene ritornato 0.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HLEN myhash
S: (integer) 2
C: HLEN anotherhash
S: (integer) 0
```

#### **HSET**

<h5> Sintassi </h5>

```plaintext
HSET <key:string> <field:string> <value:string|number>
```

Imposta il campo `field` nella hashmap identificata dalla chiave `key` al `value`. Se la chiave non esiste, viene creata una nuova hashmap. Se il campo esiste già nella hashmap, il valore viene sovrascritto. Se la chiave esiste ma non è una hashmap, viene ritornato 0. Se la chiave e il campo esistono nella hashmap ma il valore non è stringa o intero, viene ritornato 0.

<h5> Ritorno </h5>

Risposta RESP Numero: 1 se il campo è stato impostato con successo nella hashmap. 0 se la chiave esiste ma non è una hashmap o se il campo esistente nella hashmap non contiene un valore che è una stringa o un numero.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HSET myhash field1 value1
S: (integer) 1
C: HSET myhash field2 value2
S: (integer) 1
C: HSET anotherhash field1 value1
S: (integer) 1
C: HSET notanhash field1 value1
S: (integer) 0
C: HSET myhash field1 notstringornumber
S: (integer) 0
```

#### **HSTRLEN**

<h5> Sintassi </h5>

```plaintext
HSTRLEN <key:string> <field:string>
```

Restituisce la lunghezza della stringa del campo `field` specificato nella hashmap identificata dalla chiave `key`. Se la chiave non esiste, la hashmap non esiste, il campo non esiste nella hashmap o il valore non è di tipo stringa, viene restituito 0.

<h5> Ritorno </h5>

Risposta RESP Numero: La lunghezza della stringa del campo specificato nella hashmap. Se la chiave non esiste, la hashmap non esiste, il campo non esiste nella hashmap o il valore non è di tipo stringa, viene ritornato 0.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HSTRLEN myhash field1
S: (integer) 6
C: HSTRLEN myhash field2
S: (integer) 0
C: HSTRLEN anotherhash field1
S: (integer) 0
C: HSTRLEN myhash field3
S: (integer) 0
```

#### **HVALS**

<h5> Sintassi </h5>

```plaintext
HVALS <key:string>
```

Restituisce tutti i valori dei campi dalla hashmap identificata dalla chiave `key`. Se la chiave non esiste o la hashmap non esiste, viene restituito un array vuoto.

<h5> Ritorno </h5>

Risposta RESP Array: Un array di stringhe e numeri rappresentanti i valori dei campi della hashmap. Se la chiave non esiste o la hashmap non esiste, viene ritornato un array vuoto.

<h5> Esempio </h5>

> C = Client, S = Server

```plaintext
C: HVALS myhash
S: 1) "value1"
   2) "value2"
C: HVALS anotherhash
S: (empty array)
```
