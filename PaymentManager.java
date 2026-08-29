import java.util.ArrayList;

public class PaymentManager
{
    private ArrayList<Payment> Payments=new ArrayList<>();
    private int PaymentSequence=0;

    //Create
    public Payment makePayment(ParkingRecord ParkingRecordRef,String PaymentMethod) throws PaymentFailedException
    {
        if(ParkingRecordRef.getParkingFee()<=0)
        {
            throw new PaymentFailedException("Invalid amount for record "+ParkingRecordRef.getRecordId());
        }
        PaymentSequence++;
        Payment payment=new Payment(PaymentSequence,ParkingRecordRef,PaymentMethod);
        payment.processPayment();
        Payments.add(payment);
        System.out.println("Payment added successfully");
        return payment;
    }

    //Read
    public void viewPayments()
    {
        for(Payment payment:Payments)
        {
            payment.displayDetails();
            System.out.println("--------------------");
        }
    }

    //update
    public void updatePaymentStatus(int PaymentId,String NewStatus)
    {
        for(Payment payment:Payments)
        {
            if(payment.getPaymentId()==PaymentId)
            {
                payment.setPaymentStatus(NewStatus);
                return;
            }
        }
    }

    //delete
    public void deletePayment(int PaymentId)
    {
        Payments.removeIf(payment->payment.getPaymentId()==PaymentId);
    }

    //get total collected
    public double getTotalCollected()
    {
        double Total=0;
        for(Payment payment:Payments)
        {
            if(payment.getPaymentStatus().equals("Paid"))
            {
                Total+=payment.getAmount();
            }
        }
        return Total;
    }
}