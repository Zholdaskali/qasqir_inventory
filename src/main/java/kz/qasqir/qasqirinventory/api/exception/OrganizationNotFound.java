package kz.qasqir.qasqirinventory.api.exception;

public class OrganizationNotFound extends RuntimeException {
    public OrganizationNotFound() {
        super("Организация не найдена");
    }
}
