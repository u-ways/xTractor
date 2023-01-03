# xTractor

## Usage:

Given a valid log file, you can run the following to generate a report:

```sh
./gradlew run -q --args="$HOME/Downloads/{LOG_FILE_NAME}.log"
```

It will also generate the following files:
```log
./xTractor/app/build/reports/{LOG_FILE_NAME}-report.md
./xTractor/app/build/reports/{LOG_FILE_NAME}-logs-data.csv
./xTractor/app/build/reports/{LOG_FILE_NAME}-users-overview-data.csv
```

## Special flags:

Pass the `-dMediaPowerUsers` system property to the application to enable
special treatment of users who send lots of media:

```sh
./gradlew run -q -dMediaPowerUsers=Matthew,Stuart --args="$HOME/Downloads/{LOG_FILE_NAME}.log"
```
___
