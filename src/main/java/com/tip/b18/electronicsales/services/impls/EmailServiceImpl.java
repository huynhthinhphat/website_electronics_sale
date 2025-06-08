package com.tip.b18.electronicsales.services.impls;

import com.tip.b18.electronicsales.constants.MessageConstant;
import com.tip.b18.electronicsales.dto.OrderDetailDTO;
import com.tip.b18.electronicsales.entities.Account;
import com.tip.b18.electronicsales.entities.Order;
import com.tip.b18.electronicsales.exceptions.CredentialsException;
import com.tip.b18.electronicsales.exceptions.MailException;
import com.tip.b18.electronicsales.exceptions.NotFoundException;
import com.tip.b18.electronicsales.services.AccountService;
import com.tip.b18.electronicsales.services.EmailService;
import com.tip.b18.electronicsales.services.OTPService;
import com.tip.b18.electronicsales.utils.CurrencyUtil;
import com.tip.b18.electronicsales.utils.DateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final OTPService otpService;
    @Value("${spring.mail.username}")
    private String fromEmail;
    private final AccountService accountService;

    @Override
    public void sendOTP(String userName) {
        String OTP = otpService.generateOtp(userName);
        Account account = accountService.findByUserName(userName);

        String email = account.getEmail();
        if(email == null || email.isBlank()){
            throw new NotFoundException(MessageConstant.ERROR_NO_GMAIL_LINKED);
        }

        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(MessageConstant.TITLE_FORGOT_PASSWORD);
            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;">
              <table width="100%%" style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);">
                <tr>
                  <td style="padding: 30px; text-align: center;">
                    <h2 style="color: #333;">Xác minh địa chỉ email</h2>
                    <p style="font-size: 16px; color: #555;">Chào %s,</p>
                    <p style="font-size: 16px; color: #555;">Đây là mã OTP của bạn để xác minh tài khoản:</p>
                    <div style="margin: 20px 0;">
                      <span style="display: inline-block; font-size: 28px; letter-spacing: 5px; font-weight: bold; color: #FF8900;">%s</span>
                    </div>
                    <p style="font-size: 14px; color: #888;">Mã OTP này sẽ hết hạn sau 5 phút. Vui lòng không chia sẻ với bất kỳ ai.</p>
                    <p style="margin-top: 30px; font-size: 14px; color: #999;">Trân trọng,<br>Mọi thắc mắc vui lòng liên hệ: Phát Nè Hẹ Hẹ</p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(account.getFullName(), OTP);

            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        }catch (MessagingException e){
            throw new MailException(MessageConstant.ERROR_SENT_OTP);
        }
    }

    @Override
    public void verifyOTP(String userName, String OTP) {
        if(!otpService.verifyOtp(userName, OTP)){
            throw new CredentialsException(MessageConstant.ERROR_INVALID_OTP);
        }
        otpService.deleteOtpInRedis(userName);
    }

    @Override
    @Async
    public void sendBill(String fullName, String email, Order order, List<OrderDetailDTO> orderDetailDTOList) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject(MessageConstant.TITLE_ORDER.formatted(order.getOrderCode()));

            StringBuilder stringBuilder = new StringBuilder();
            for(OrderDetailDTO orderDetail : orderDetailDTOList){
                stringBuilder.append(String.format("""
                        <tr>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                            <td style="border: 1px solid #ddd; padding: 12px;">%s</td>
                        </tr>
                        """, orderDetail.getSku(), orderDetail.getName(), orderDetail.getColor(), orderDetail.getQuantity(), CurrencyUtil.formatCurrency(orderDetail.getPrice()), CurrencyUtil.formatCurrency(orderDetail.getTotalPrice())));
            }

            String htmlContent = """
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px;">
              <div style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; padding: 20px;">
                <h2 style="color: #4CAF50;">🎉 Đặt hàng thành công!</h2>
                <p>Xin chào <strong>%s</strong>,</p>
                <p>Bạn đã đặt hàng thành công tại <strong>Electronics Sales</strong>.</p>
                <h3>Thông tin đơn hàng</h3>
                <ul>
                  <li><strong>Mã đơn hàng:</strong> %s</li>
                  <li><strong>Ngày đặt:</strong> %s</li>
                  <li><strong>Phương thức thanh toán:</strong> %s</li>
                  <li><strong>Địa chỉ nhận hàng:</strong> %s</li>
                  <li><strong>Phí ship:</strong> %s</li>
                  <li><strong>Tổng tiền:</strong> %s</li>
                </ul>
                <h3>Thông tin sản phẩm</h3>
                <table style="border-collapse: collapse; width: 100%%; margin-top: 20px;">
                    <thead>
                        <tr style="background-color: #f2f2f2;">
                            <th style="border: 1px solid #ddd; padding: 12px;">Mã sản phẩm</th>
                            <th style="border: 1px solid #ddd; padding: 12px;">Sản phẩm</th>
                            <th style="border: 1px solid #ddd; padding: 12px;">Màu sắc</th>
                            <th style="border: 1px solid #ddd; padding: 12px;">Số lượng</th>
                            <th style="border: 1px solid #ddd; padding: 12px;">Đơn giá</th>
                            <th style="border: 1px solid #ddd; padding: 12px;">Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        %s
                    </tbody>
                </table>
                <p>Cảm ơn bạn đã mua sắm cùng chúng tôi!</p>
                <p style="margin-top: 30px;">Trân trọng,<br><strong>Electronics Sales Team</strong></p>
                <hr style="margin-top: 30px;">
                <small style="color: #888;">Email này được gửi tự động. Vui lòng không trả lời.</small>
              </div>
            </body>
            </html>
            """.formatted(fullName, order.getOrderCode(), DateUtil.formatDate(order.getCreatedAt()), order.getPaymentMethod().getDisplayName(), order.getAddress(), CurrencyUtil.formatCurrency(new BigDecimal(15000)), CurrencyUtil.formatCurrency(order.getTotalPrice()), stringBuilder);

            mimeMessageHelper.setText(htmlContent, true);
            javaMailSender.send(mimeMessage);
        }catch (MessagingException e){
            e.printStackTrace();
        }
    }
}
