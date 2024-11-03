# Challenge 2: Backend – Predict next 3 values of Stock price (timeseries data)
This project is addressed to a specific requestor. Represent a POC Spring Boot REST API microservice based on document `TechChallenge2.pdf` which from possible copyright issues, I will not upload it in this repo.

## Setup
Maven Spring Boot project, with prerequisites:
- Java 17+
- Maven ver 3+

`application.yaml` has some notable keys:
```yaml
server:
  port: 8090
  ssl:
    enabled: false
# ...
# ...
app:
  dataFolder:
    in: C:/dev/cata/lseg/stock_price_data_files
    out: C:/dev/cata/lseg/out
  samplesCount: 10
  predictor: basic
  predictedSamplesCount: 3
  auth:
    enabled: false
# ...
# ...
logging:
  level:
    root: info
```

| Param                    | Description                                  |
|--------------------------|----------------------------------------------|
| `server.port`            | Application default web port                 |
| `server.ssl.enable`      | Enable SSL access - see below                |
| `app.dataFolder.in`      | The root folder containing market folders    |
| `app.dataFolder.out`     | The root folder of generated files           |
| `app.samplesCount`       | How many lines will be sampled from one file |
| `app.predictor`          | Predictor engine - see below                 |
| `app.predictedSamplesCount` | The prediction horizon to be made         |
| `app.auth.enabled`       | Enable basic auth - see below                |
| `logging.level.root`     | Change logging level - see below             |

**Mandatory to set `app.dataFolder.in` and `app.dataFolder.out` with tester own values**

Expose:
- Server:  default port 8090
- Swagger: http://localhost:8090/swagger-ui/index.html
- API: http://localhost:8090/api/**

## Build
Build fat jar:
 ```shell
# from ./stock-predict dir
 mvn clean package
```

## Run
```shell
java -jar target/stock-predict-0.0.1-SNAPSHOT.jar
#or
mvn spring-boot:run
```

Open swagger for API documentation:
 - http://localhost:8090/swagger-ui/index.html 

List of exposed end points:
1. `GET /api/sample` has a single web query param `file` which contain absolut or relative path (to `app.dataFolder.in` defined path) for  data file.

```shell
curl http://localhost:8090/api/sample?file=LSE/FLTR.csv


{
  "symbol": "FLTR",
  "countPoints": 10,
  "generatedDate": "2024-11-03 20:09:36",
  "pricePointList": [
    {
      "date": "2023-11-20",
      "price": 17858.68
    },
    {
      "date": "2023-11-21",
      "price": 18090.84
    },
    {
      "date": "2023-11-22",
      "price": 18018.48
    },
    {
      "date": "2023-11-23",
      "price": 18216.68
    },
    {
      "date": "2023-11-24",
      "price": 18271.33
    },
    {
      "date": "2023-11-25",
      "price": 18289.6
    },
    {
      "date": "2023-11-26",
      "price": 18435.92
    },
    {
      "date": "2023-11-27",
      "price": 18325.3
    },
    {
      "date": "2023-11-28",
      "price": 18398.61
    },
    {
      "date": "2023-11-29",
      "price": 18361.81
    }
  ]
}
```

Example of request with `file` which not exists:
```shell
curl http://192.168.0.229:8090/api/sample?file=LSE/FLTR-DUMMY.csv


{
  "className": "org.cata.lseg.stockpredict.exception.NoFilesException",
  "exMessage": "Not exist: C:\\dev\\cata\\lseg\\stock_price_data_files\\LSE\\FLTR-DUMMY.csv",
  "httpStatus": "NOT_FOUND",
  "uuid": "6da50cf3-cce6-46a8-8780-9a3c5db62159",
  "path": "/api/sample",
  "timestamp": "2024-11-03T21:40:54.985661"
}
```

2. `POST /api/predict` take a json with sample data and return `app.predictedSamplesCount` number of predicted values and will write a full sample file on `app.dataFolder.out` (file will contain original send sample + new predicted ones)

```shell
curl -X POST --location "http://localhost:8090/api/predict" -H "Content-Type: application/json" -d '{"symbol":"FLTR","pricePointList":[{"date":"2023-09-13","price":16220.25},{"date":"2023-09-14","price":16171.59},{"date":"2023-09-15","price":16106.91},{"date":"2023-09-16","price":16058.58},{"date":"2023-09-17","price":16283.4},{"date":"2023-09-18","price":16397.39},{"date":"2023-09-19","price":16561.36},{"date":"2023-09-20","price":16644.17},{"date":"2023-09-21","price":16793.97},{"date":"2023-09-22","price":16260.38}],"countPoints":10,"generatedDate":"2024-11-01 19:59:50"}'


{
  "symbol": "FLTR",
  "countPoints": 3,
  "generatedDate": "2024-11-03 20:17:57",
  "pricePointList": [
    {
      "date": "2023-09-22",
      "price": 16126.64
    },
    {
      "date": "2023-09-23",
      "price": 16031.04
    },
    {
      "date": "2023-09-24",
      "price": 16260.38
    }
  ]
}
```

3. `GET /api/scan` will scan `app.dataFolder.in` and retrive a list of `count` files from each folder. Each file will be sampled and will be generated a new one with prediction and original data in `app.dataFolder.out`  

```shell
curl http://localhost:8090/api/scan?count=1


{
  "generatedDate": "2024-11-03 20:37:35",
  "files": [
    "C:\\dev\\cata\\lseg\\out\\FLTR.csv",
    "C:\\dev\\cata\\lseg\\out\\TSLA.csv",
    "C:\\dev\\cata\\lseg\\out\\ASH.csv"
  ]
}
```

Example of request with wrong `count` value:
```shell
curl http://localhost:8090/api/scan?count=100


{
  "className": "org.cata.lseg.stockpredict.exception.FileCounterException",
  "exMessage": "The count parameter must be between 1 and 2",
  "httpStatus": "BAD_REQUEST",
  "uuid": "02c513e7-365a-4226-8fed-d6c27ca5c022",
  "path": "/api/scan",
  "timestamp": "2024-11-03T20:37:16.9193209"
}
```

## SSL support
Default TLS/SSL auth is disabled, to enable it:
```shell
java -jar target/stock-predict-0.0.1-SNAPSHOT.jar --server.ssl.enabled=true
```

Keys pair used are self-signed certificates for testing purpose, therefor not signed by an authority. Opening in browser will be warned by a **not trusted certificate found**, please ignore it. To bypass this issue in `curl`, please add `-k` switch and call of course `https`:

```shell
curl -k https://localhost:8090/api/sample?file=LSE/FLTR.csv
```

## Basic auth support
Default Spring auth is disabled, to enable it:
```shell
java -jar target/stock-predict-0.0.1-SNAPSHOT.jar --app.auth.enabled=true
```

Existing accounts:
    - admin:admin
    - user:user

For the sake of demo, I have applied an `ROLE_ADMIN` on `/api/predict` end point. Accessing api with basic auth enabled:

```shell
curl -u user:user http://localhost:8090/api/sample?file=LSE/FLTR.csv
curl -u admin:admin http://localhost:8090/api/scan
```

## Predictor engine
There are to engines (aka algorithms) implemented:
1. **BASIC** - The default propose algo in documentation
    ```yaml
    app:
      predictor: basic
    ```
2. **MA** - based on moving average algo, it uses **Apache Commons Maths** lib
    ```yaml
    app:
      predictor: ma
    ```

## Enable debug loging
```shell
java -jar target/stock-predict-0.0.1-SNAPSHOT.jar --logging.level.root=debug
```

## TODOs
Next on improvements:
- **tests**: it has very basic tests, need serious improvement here
- **security**: password encryption to be implemented
- **exceptions**: add and refined
- implement of full **OpenAPI**: not necessary right now, but for future extensibility
