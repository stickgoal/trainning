package me.maiz.ittrainning.restdemo.controller;

import me.maiz.ittrainning.restdemo.entity.Card;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/card")
public class CardController {

    @PostMapping("/")
    public boolean addCard(@RequestBody Card card) {

        return false;
    }

    @GetMapping("/{id}")
    public Card findById(@PathVariable("id") int id,@PathVariable("action") String action) {
        System.out.println(action);
        return new Card(id,"lina","lucas",new Date());
    }

    @PutMapping("/")
    public boolean update(@RequestBody Card Card) {
        return true;
    }

    @DeleteMapping("/{id}") //  /card/1001
    public boolean delete(@PathVariable("id") int id) {
        return true;
    }

}
