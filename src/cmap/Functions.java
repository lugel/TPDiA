package cmap;
import java.lang.Math;
import java.util.List;

public class Functions {
	
	public double FisherZ(double r) {
		double z=(1/2)*(Math.log((1+r)/(1-r)));	
		return z;
	}

	public double corr(List<Double> a, List<Double> b) {
		double r=0;
		double sumA=0;
		double sumB=0;
		double avgA=0;
		double avgB=0;
		double nominatorSum=0;
		double denominatorSumA=0;
		double denominatorSumB=0;
		
		for(int i=0;i<a.size();i++) {
			sumA+=a.get(i);
		}
		
		avgA=sumA/a.size();
		
		for(int i=0;i<b.size();i++) {
			sumB+=b.get(i);
		}
		
		avgB=sumB/b.size();
		
		
		for(int i=1;i<a.size();i++) {
			nominatorSum+=(a.get(i)-avgA)*(b.get(i)-avgB);
		}
		
		for(int i=1;i<a.size();i++) {
			denominatorSumA+=(a.get(i)-avgA)*(a.get(i)-avgA);
		}
				
		
		for(int i=1;i<b.size();i++) {
			denominatorSumB+=(b.get(i)-avgB)*(b.get(i)-avgB);
		}
		
		r=nominatorSum/(Math.sqrt(denominatorSumA)*Math.sqrt(denominatorSumB));
				
		return r;
	}
}
