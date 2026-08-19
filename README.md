# 🚗 Distributed Ride-Matching Engine (like Uber,Ola)

A high-throughput, low-latency distributed system built with **Spring Boot**, **Apache Kafka**, **Redis**, and **PostgreSQL** that simulates real-time driver matching and location tracking at scale.

---

## 📌 Problem Statement

In ride-hailing applications like Uber or Ola, thousands of drivers stream their location updates every second. 

Using traditional SQL databases to calculate spatial distances (e.g., using Haversine or `ST_Distance`) over frequently changing GPS coordinates leads to severe bottlenecks:
- High latency during read/write queries
- Table locks and database overhead
- Thread blocking under high concurrency

---

## ⚡ The Solution

This project solves spatial scalability and event handling by decoupling data persistence, fast in-memory spatial lookups, and asynchronous messaging across **3 dedicated microservices**:

1. **Ride-Service (Spring Boot + PostgreSQL + Kafka):**
   - Manages ride lifecycles and stores persistent ride/user metadata in **PostgreSQL**.
   - **Publishes** `ride.requested` events to Kafka.
   - **Consumes** `driver.found` events from Kafka to update ride statuses in real time.

2. **Location-Service (Spring Boot + Redis Geospatial):**
   - Indexes live driver GPS coordinates into **Redis** using `GEOADD`.
   - Executes sub-millisecond proximity searches using `GEOSEARCH` (e.g., finding drivers within 5km).

3. **Matching-Service (Spring Boot + Kafka):**
   - Consumes `ride.requested` events asynchronously from Kafka.
   - Queries **Location-Service** for candidate drivers, runs matching logic, and **publishes** `driver.found` events back to Kafka.

---

## 🛠️ Tech Stack

- **Framework:** Java, Spring Boot, Spring Data JPA
- **Event Streaming:** Apache Kafka, Zookeeper
- **In-Memory Caching & Geospatial Indexing:** Redis (`GEOADD`, `GEOSEARCH`)
- **Primary Database:** PostgreSQL
- **Build Tool:** Maven / Gradle

---

## 🔄 Event-Driven Workflow

```text
[Rider App] --(REST)--> [Ride-Service] --(Publishes: ride.requested)--> [Kafka]
                                                                          |
                                                                   (Consumes Event)
                                                                          |
                                                                          v
[Redis Geo] <--(Query Nearby)--> [Location-Service] <------------ [Matching-Service]
                                                                          |
                                                                    (Executes Match)
                                                                          |
                                                                          v
[PostgreSQL] <--(Update Status)-- [Ride-Service] <--(Publishes: driver.found)--|
