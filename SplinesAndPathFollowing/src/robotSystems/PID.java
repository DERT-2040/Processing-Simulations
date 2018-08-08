package robotSystems;

public class PID 
{
	
	double resultant = 0;
	double error = 0;
	double pError = 0;
	double derivative = 0;
	double integral = 0;
	double P;
	double I;
	double D;
	double setpoint;
	
	public PID(double P_GAIN, double I_GAIN, double D_GAIN)
	{
		P = P_GAIN;
	    I = I_GAIN;
	    D = D_GAIN;
	}
	
	public void setSetpoint(double sPoint) 
	{
		setpoint = sPoint;
	}
	
	public double getResultant(double input) 
	{
		pError = error;
		
		error = setpoint - input;
		
		derivative = (error - pError)/(1/.016);
		
		integral += error*(1/.016);
		
		resultant = P*error + I*integral + D*derivative;
		
		return resultant;
	}
	
	
}
