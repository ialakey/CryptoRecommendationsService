# Crypto Recommendations Service

## Description

This service provides an API for fetching cryptocurrency statistics, prices, and other related data. With this service, you can track cryptocurrency prices in real-time, get stats about various cryptocurrencies, and work with historical data.

## API Documentation

The API documentation is available through Swagger UI at the following address:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Swagger UI provides an interactive interface to explore the API: you can view all available endpoints and try them in action.

## Endpoints

### 1. Get a list of cryptocurrencies sorted by normalized range

- **URL**: `/crypto/stats`
- **Method**: `GET`
- **Description**: Returns a list of cryptocurrencies with their normalized ranges.

### 2. Get the latest price data for a specific cryptocurrency

- **URL**: `/crypto/{symbol}/latest`
- **Method**: `GET`
- **Description**: Returns the latest price data for a given cryptocurrency symbol.

### 3. Get the old, new, minimum, and maximum prices of a cryptocurrency

- **URL**: `/crypto/{symbol}/stats`
- **Method**: `GET`
- **Description**: Returns the statistics for a cryptocurrency, including old, new, minimum, and maximum prices.

### 4. Get the cryptocurrency with the highest normalized range for a specific day

- **URL**: `/crypto/top`
- **Method**: `GET`
- **Description**: Returns the cryptocurrency with the highest normalized range for the given day.

### 5. Support for historical data (6 months / 1 year)

- **URL**: `/crypto/{symbol}/history`
- **Method**: `GET`
- **Description**: Returns historical price data for a specific cryptocurrency over the past months. You can specify the number of months (default is 1).

- **Parameters**:
    - `symbol` (path parameter): The symbol of the cryptocurrency (e.g., BTC, ETH).
    - `months` (query parameter, optional): The number of months of historical data to fetch (default is 1).

## Running the Service with Docker

To run the service using Docker, follow these steps:

1. Build the Docker image:

   ```bash
   docker build -t crypto-recommendations-service .
   ```

   ```bash
   docker run -p 8080:8080 crypto-recommendations-service
   ```