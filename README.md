# SmartQueue

**Team Members:** Steven Rodriguez, Joel Plew, Alex Dominguez

---

## Overview

Many small to medium-sized services—such as clinics, offices, and government service centers—struggle with long wait times, overcrowded waiting areas, and inefficient appointment management. 

Customers often arrive without knowing how long they might have to wait, leading to frustration and missed appointments. Simultaneously, staff members face difficulties managing walk-ins, cancellations, and schedule changes in real time. This systemic friction results in wasted time for customers, reliance on manual or outdated scheduling systems for staff, and overarching inefficiencies that degrade the customer experience.

**SmartQueue** is a web-based application designed to solve this by managing appointments and virtual queues in real time. The system allows customers to schedule appointments, join a virtual waiting list, and view estimated wait times remotely, drastically reducing the need for physical waiting areas.

By utilizing Agile development techniques, SmartQueue is built to be incrementally improved based on user feedback, ensuring it remains flexible and continuously meets real-world needs.

## Value Proposition

SmartQueue provides immediate value by:
* **Improving the Customer Experience:** Reducing physical wait times and providing timely notifications.
* **Increasing Operational Efficiency:** Automating scheduling for service providers, dynamically handling walk-ins, and reducing no-shows.
* **Enabling Data-Driven Decisions:** Generating real-time analytics on wait times, peak hours, and overall service performance.

## Target Users & Stakeholders

The system is designed to support multiple user groups, each with distinct roles and needs:

### Primary Users
* **Customers/Clients:** Individuals who schedule appointments, join virtual queues, receive notifications, and view live estimated wait times.
* **Staff:** Employees who manage the active floor, serve customers, update queue statuses, and track appointment progress.

### Secondary Users
* **Administrators:** Personnel responsible for configuring services, managing schedules, and generating reports.

### Stakeholders
* **Business Owners:** Focused on improved efficiency, reduction of no-shows, and higher customer satisfaction metrics.
* **IT (Information Technology):** Responsible for system maintenance, security, and integration with existing infrastructure.

## Core Features

1.  **Online Appointment Scheduling:** Users can book or cancel appointments through a responsive web interface.
2.  **Virtual Queue Management:** Users join a virtual waiting list and can view their current position in the queue at any time.
3.  **Real-Time Wait Time Estimation:** The system provides live estimates of wait times based on current queue depth and dynamic service duration settings.
4.  **Automated Notifications:** WebSockets provide instant, real-time alerts to users when it is their turn to be served.
5.  **Staff Management Dashboard:** A secure portal where staff can view the full daily roster, manage the flow of the queue, and update global service settings in real time.

## Tech Stack

* **Backend:** Java 21, Spring Boot 3.2.3, Spring Security, Spring Data JPA
* **Frontend:** HTML5, CSS3, Vanilla JavaScript (ES6+), Fetch API
* **Real-Time Communication:** WebSockets, STOMP, SockJS
* **Database:** H2 (File-based local storage)
* **Build Tool:** Maven

## Getting Started

### Prerequisites
* Java Development Kit (JDK) 21
* Maven installed locally (or use your IDE's embedded Maven)

### Building and Running the Application
1. **Clone the repository** to your local machine.
2. **Build the project** using Maven to download dependencies and create the executable JAR:
   ```bash mvn clean package```
3. **Run the appiacation** ```bash java -jar target/SmartQueue-3.0.jar```
4. The database will automatically initialize via the application.properties configuration.
5. Access the Customer Booking Station at: http://localhost:8080/
6. Access the Staff Portal at: http://localhost:8080/employee.html (Login: staff / pass)

### Easy Method
1. **Download the JAR file** to your local machine
2. **Run the appication** ```java -jar /PATH_TO_JAR_FILE```
3. The database will automatically initialize via the application.properties configuration.
4. Access the Customer Booking Station at: http://localhost:8080/
5. Access the Staff Portal at: http://localhost:8080/employee.html (Login: staff / pass)