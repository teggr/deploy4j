package com.robintegg.deploy4j.springbootwebapplicationsample;

import org.springframework.hateoas.CollectionModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class RestApiController {

  @GetMapping("/messages")
  public CollectionModel<Message> getMessages() {
    List<Message> messages = List.of(new Message("hi there"));
    CollectionModel<Message> model = CollectionModel.of(messages);
    model.add(linkTo(methodOn(RestApiController.class).getMessages()).withSelfRel());
    return model;
  }

}
