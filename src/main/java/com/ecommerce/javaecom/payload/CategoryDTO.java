package com.ecommerce.javaecom.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Long categoryId;
    private String categoryName;
}

/*
why are DTO (Data Transfer Object) needed?

We want to decouple Model with our presentation layer
Models/Entities should only handle databases, it should not
handle things like how data is presented to the client
ex. things like Pagination, hiding details in frontend etc.

We should have a layer which process these requests and
modifies the information that is being sent to the client

Hence DTO are needed, we would need DTO since server would
need one to intercept data from DB to client and the another
one would be needed when client sends a request to server
this would be sent to DTO first and then to the DB

 */