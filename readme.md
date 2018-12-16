# Net Pay App



Net Pay is a simple app consisting of spring-boot backend and angular front end that allows user to calculate net pay (in PLN) in different countries based on fixed cost amount and tax rate. 

# Usage

 - prerequisites
 - - npm 
 - - maven
 - - java jdk

 - check out both modules (NetPay, sonalake-task-gui)
build & run backend :
```sh
$ cd NetPay
$ mvn clean spring-boot:run
```

 - build & run frontend :

```sh
$ cd sonalake-task-gui
$ ng serve
```
 - open browser at http://localhost:4200/
 - fill in rates and see response

# Configuration
Application configuration is maintained in yaml format under application.yml file.Following parameters are used:
 - **nbpApiUrl** - url of nbp service providing exchange rates
 - **ratesForOfflineModeFileName** - name of the file with some historic rates in case if nbp service is unavailable
 - **nbpApiKnownCurrencies** - list of currencies that application can handle
 - **countries** - map of country configuration.  If new country needs to be handled by application it needs to be added to that map and a new currency code to param **nbpApiKnownCurrencies**
 
 # Application internals
Two classes contain logic of application , namely **NetPayController** and **CurrencyRatesFetcher**.
 - **NetPayController** - entry point of rest requests. Processes input , and calculates the net pay based on configuration, currency rates fetched by **CurrencyRatesFetcher** class and daily rates from request. Simple request validation is also handled in this class
 - **CurrencyRatesFetcher** - fetches the data from NBP service (or static file if the service is unavailable). Results of invoking this class method **fetchLatest()** are cached to speed up processing time and avoid redundant network traffic
  



