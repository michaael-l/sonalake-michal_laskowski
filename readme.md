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



