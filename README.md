# Palestra

App Android per tracciare gli allenamenti in palestra.

Registra pesi e ripetizioni per ogni esercizio, divisi in 3 giornate:
- **Giorno 1** – Petto & Bicipiti
- **Giorno 2** – Gambe & Spalle
- **Giorno 3** – Dorso & Tricipiti

## Funzionalità

- **Allenamento**: mostra il giorno successivo con i pesi precompilati dall'ultima volta. Checkbox per segnare gli esercizi fatti.
- **Configurazione**: aggiungi, rimuovi o rinomina esercizi per ogni giorno.
- **Riepilogo**: tabella cronologica con tutti i pesi. Le celle arancioni indicano un aumento di carico.
- **Backup**: importa/esporta i dati in formato JSON.

I dati vengono salvati automaticamente nella memoria interna del telefono (`dati-palestra.json`).

## Build

```bash
./gradlew assembleDebug
```

L'APK si trova in `app/build/outputs/apk/debug/app-debug.apk`.
