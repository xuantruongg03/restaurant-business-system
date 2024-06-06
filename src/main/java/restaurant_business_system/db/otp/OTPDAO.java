package restaurant_business_system.db.otp;

import org.jdbi.v3.core.Jdbi;

public class OTPDAO {
    private final Jdbi jdbi;

    public OTPDAO(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public boolean create(OTP otp){
        jdbi.useHandle(handle->{
            handle.createUpdate("Insert into otps(phone, otp_code) values (?, ?)")
                    .bind(0, otp.getPhone())
                    .bind(1, otp.getOtp_code())
                    .execute();
        });
        return false;
    }
    public boolean checkOTP(OTP otp) {
        return jdbi.withHandle(handle -> {
            int updated = handle.createUpdate("UPDATE otps SET used = 1 WHERE phone = :phone AND otp_code = :otpCode")
                    .bind("phone", otp.getPhone())
                    .bind("otpCode", otp.getOtp_code())
                    .execute();
            return updated > 0;
        });
    }
    
}
