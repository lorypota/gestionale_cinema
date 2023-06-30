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

***Sintassi ed esempi si basano sull'input/output del tool `redis-cli`, encoding RESP adeguato dovrebbe essere usato.***
***Syntax and examples are based on `redis-cli` input/output, proper RESP encoding should be used.***

### Stringhe semplici RESP

Le Stringhe semplici sono codificate come segue: il carattere più, seguito da una stringa che non può contenere un carattere CR o LF (i newlines non sono ammessi), e terminate da CRLF (ovvero "\r\n").

Le Stringhe semplici sono usate per trasmettere stringhe non bynary-safe con un overhead minimo. Per esempio, alcuni comandi rispondono solo con "OK" se hanno avuto successo. Questa String semplice è codificata con i 5 byte seguenti:

```plaintext
"+OK\r\n"


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

## Comandi utili

### **COMMAND**

<h4>Syntax<h4>

```plaintext
COMMAND [command:string]
```

Return an array with details about every Server  command.

The command's array consists of a fixed number of elements. The exact number of elements in the array depends on the server's version.

- Name --> This is the command's name.
- Arity --> Arity is the number of arguments a command expects. It follows a simple pattern:
  - A positive integer means a fixed number of arguments.
  - A negative integer means a minimal number of arguments.
Command arity always includes the command's name itself (and the subcommand when applicable).
- Flags --> N/A
- First key --> The position of the command's first key name argument. For most commands, the first key's position is 1. Position 0 is always the command name itself.
- Last key --> The position of the command's last key name argument.
- Step --> The step, or increment, between the first key and the position of the next key.

<h4> Returns </h4>

Array reply: a nested list of command details.

The order of commands in the array is random.

<h4> Example </h4>

The following is COMMAND's output for the GET command:

```plaintext
1) "get"
2) (integer) 2
3) (empty array)
4) (integer) 1
5) (integer) 1
6) (integer) 1
```

### **PING**

<h4>Syntax<h4>

```plaintext
PING
```

Returns PONG.
This command is useful for:

1. Testing whether a connection is still alive.
2. Measuring latency.

<h4> Returns </h4>

Simple string reply: PONG

<h4> Example </h4>

> C = Client, S = Server

```plaintext
C: PING
S: PONG
```

## Comandi Strings

## Comandi Integers

### **DECR**

<h4> Syntax <h4>

```plaintext
DECR <key:string>
```

Decrements the number stored at key by one. If the key does not exist, it is set to 0 before performing the operation. An error is returned if the key contains a value of the wrong type. This operation is limited to 32 bit signed integers.

<h4> Returns </h4>

Integer reply: the value of key after the decrement

<h4> Example </h4>

> C = Client, S = Server

```plaintext
C: DECR key
S: -1
C: DECR key
S: -2
```

## Comandi Hashes
