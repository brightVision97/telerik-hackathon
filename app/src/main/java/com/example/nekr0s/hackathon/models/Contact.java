package com.example.nekr0s.hackathon.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Contact {
    private String name;
    private String number;
    private String address;
}
