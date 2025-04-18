package ma.enset.hopital.security.service;


import ma.enset.hopital.entities.AppRole;
import ma.enset.hopital.entities.AppUser;

public interface AccountService {
    AppUser addNewUser(String username, String password, String confirmPassword);
    AppRole addNewRole(String roleName);
    void addRoleToUser(String username, String roleName);
    AppUser loadUserByUsername(String username);
}
