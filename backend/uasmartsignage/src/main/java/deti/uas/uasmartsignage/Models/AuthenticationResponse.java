package deti.uas.uasmartsignage.Models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    
    private final String jwt;
}
