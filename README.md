## *This is the alpha version of the project.*
### Tech stack  
* Java 17
* Spring Boot (web, security, data)  
* PostgreSql (via hibernate) for user and subscriptions details
* Clickhouse (jdbc) for big data and olap requests 
* Docker
* [Eventeum](https://github.com/eventeum) with a modified technical part  

## About the project  
![image](https://github.com/Remittimus/BlockchainAnalysisSystem/assets/56563715/2912b977-0720-48a1-933b-36886f819b86)  

This is a full-fledged website with the ability to register, subscribe and track transactions with the display of statistics. All you need is access to an Ethereum node and an address whose outgoing or incoming transactions you are interested in.  

![image](https://github.com/Remittimus/BlockchainAnalysisSystem/assets/56563715/0229ccb8-5fee-440f-b45f-3485e251766b)  

By creating a subscription and setting filters for it, you will start receiving transactions that match your conditions.  
Please note that information will not appear immediately. On average, one block per ethereum is formed in about 10 seconds.  
The information is contained in the tables, which you can see by clicking on the subscription address. Don't be afraid of a lot of information, you can always turn off unnecessary columns and sort the data (if you haven't already).  

![image](https://github.com/Remittimus/BlockchainAnalysisSystem/assets/56563715/f1954255-3f0b-42d2-b559-4bda8dd0a383)

You can also see statistics in the form of graphs by subscription.  
![image](https://github.com/Remittimus/BlockchainAnalysisSystem/assets/56563715/d95fd5c8-0265-4614-8103-adb6bb512fa8)



If you change your mind or just want to stop tracking this address, one click on the cross will be enough to erase all information.

### Build
1. Set your ethereum node address in ./server/docker-compose.yaml.  
 ![image](https://github.com/Remittimus/BlockchainAnalysisSystem/assets/56563715/62ca49f0-72de-47c8-ae5a-5839868ab0b1)  
 
If you don't have an address, you can always get an api-key from infura or alchemy.  

2. Build and up project

```sh
$ cd server
$ docker-compose -f docker-compose.yml build
$ docker-compose -f docker-compose.yml up
```
