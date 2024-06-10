package restaurant_business_system.db.account;

public class AccountEdit {
    private String name;
    private String numberPhone;
    private String birthDate;
    private String email;

    public AccountEdit (String name, String numberPhone, String birthDate, String email) {
        this.name =  name;
        this.numberPhone = numberPhone;
        this.birthDate = birthDate;
        this.email = email;
    }

    public String getName () {
        return this.name;
    }

    public String getNumberPhone () {
        return this.numberPhone;
    }

    public String getBirthDate () {
        return this.birthDate;
    }

    public String getEmail () {
        return this.email;
    }
}
