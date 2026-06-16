package com.api.capas.infrastructure.web;

import com.api.capas.domain.dto.RegistroDTO;
import com.api.capas.domain.dto.LoginDTO;
import com.api.capas.domain.dto.LoginResponseDTO;
import com.api.capas.infrastructure.persistence.entities.Usuario;
import com.api.capas.application.service.UsuarioService;
import com.api.capas.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    public ResponseEntity<Usuario> registrar(@RequestBody RegistroDTO dto) {
        Usuario nuevoUsuario = usuarioService.registrar(dto);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new LoginResponseDTO(jwt, "Login exitoso", userDetails.getUsername()));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(401).body(new LoginResponseDTO(null, "Datos incorrectas"));
        }
    }
}