package ir.maktabsharif.home_service.dto.customer;

import ir.maktabsharif.home_service.dto.ValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CustomerSaveUpdateRequest {
    @NotNull(groups = {ValidationGroup.Update.class})
    private Integer id;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String firstName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    private String lastName;
    @NotBlank(groups = {ValidationGroup.Save.class})
    @Email
    private String email;
    @NotBlank(groups = {ValidationGroup.Save.class})
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and contain both letters and numbers.",
            groups = {ValidationGroup.Save.class,ValidationGroup.Update.class})
    private String password;

    public @NotNull(groups = {ValidationGroup.Update.class}) Integer getId() {
        return id;
    }

    public void setId(@NotNull(groups = {ValidationGroup.Update.class}) Integer id) {
        this.id = id;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getFirstName() {
        return firstName;
    }

    public void setFirstName(@NotBlank(groups = {ValidationGroup.Save.class}) String firstName) {
        this.firstName = firstName;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) String getLastName() {
        return lastName;
    }

    public void setLastName(@NotBlank(groups = {ValidationGroup.Save.class}) String lastName) {
        this.lastName = lastName;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) @Email String getEmail() {
        return email;
    }

    public void setEmail(@NotBlank(groups = {ValidationGroup.Save.class}) @Email String email) {
        this.email = email;
    }

    public @NotBlank(groups = {ValidationGroup.Save.class}) @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and contain both letters and numbers.",
            groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(groups = {ValidationGroup.Save.class}) @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$",
            message = "Password must be at least 8 characters long and contain both letters and numbers.",
            groups = {ValidationGroup.Save.class, ValidationGroup.Update.class}) String password) {
        this.password = password;
    }
}
