package restaurant_business_system.api;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import restaurant_business_system.db.account.AccountDAO;
import restaurant_business_system.db.otp.OTP;
import restaurant_business_system.db.otp.OTPDAO;
import restaurant_business_system.db.otp.PhoneNumberRequest;
import restaurant_business_system.helper.OTPHelper;
import restaurant_business_system.response.ApiResponse;
import io.github.cdimascio.dotenv.Dotenv;


@Path("/otp")
@Produces(MediaType.APPLICATION_JSON)
public class OTPAPI {
    private static final Dotenv dotenv = Dotenv.load();
    private final String ACCOUNT_SID = dotenv.get("TWILIO_ACCOUNT_SID");
    private final String AUTH_TOKEN = dotenv.get("TWILIO_AUTH_TOKEN");
    private final String PHONE = dotenv.get("TWILIO_PHONE");

    private final OTPDAO otpDao;
    private final AccountDAO accountDao;

    public OTPAPI(OTPDAO otpDao, AccountDAO accountDao) {
        this.otpDao = otpDao;
        this.accountDao = accountDao;
    }

    @POST
    @Path("/sendOTP")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendOTP(PhoneNumberRequest request) {
        try {
            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            String code_otp = OTPHelper.generateOTP(5);
            Message message = Message.creator(
                    new PhoneNumber(request.getPhone()), // Số điện thoại nhận
                    new PhoneNumber(PHONE), // Số điện thoại gửi của Twilio
                    "Your OTP code is: " + code_otp  // Nội dung tin nhắn
            ).create();
            message.getSid();
            if(otpDao.create(new OTP(request.getPhone() , code_otp))){
                ApiResponse apiResponse = new ApiResponse(true, "Operation succeeded");
                return Response.ok(apiResponse).build();
            }
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Failed to send OTP.\"}").build();
        } catch (Exception e) {
            System.err.println(e);
            return Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Failed to send OTP.\"}").build();
        }
    }
    
    @POST
    @Path("/activeOTP")
    @Produces(MediaType.APPLICATION_JSON)
    public Response activeOTP(OTP otp){
        try {
            if(!OTPHelper.isOTP(otp.getOtp_code())){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid OTP")
                        .type(MediaType.APPLICATION_JSON)
                        .build();
            }
            // Check OTP
            if(otpDao.checkOTP(otp)){
                if(accountDao.activeAcount(otp.getPhone())){
                    ApiResponse apiResponse = new ApiResponse(true, "Operation succeeded");
                return Response.ok(apiResponse).build();
                }
                System.out.println("12312313");
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.APPLICATION_JSON)
                .build();
            }
            return Response.status(Response.Status.UNAUTHORIZED)
                            .entity("OTP is not valid")
                            .type(MediaType.APPLICATION_JSON)
                            .build();
            
        } catch (Exception e) {

            System.out.println(e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .build();
        }        
    }

    @POST
    @Path("/checkOTP")
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkOTP(OTP otp){
        try {
            if(otpDao.checkOTP(otp)){
                ApiResponse apiResponse = new ApiResponse(true, "Operation succeeded");
        return Response.ok(apiResponse).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .build();
        } catch (Exception e) {
            // TODO: handle exception
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .type(MediaType.APPLICATION_JSON)
            .build();
        }
    }

}
