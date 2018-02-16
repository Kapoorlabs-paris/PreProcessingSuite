package snakeSegmentation;

import java.util.ArrayList;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ColorProcessor;
import interactivePreprocessing.InteractiveMethods;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Pair;
import net.imglib2.util.ValuePair;
import utility.Convolvers;
import utility.Interpolator;

public class SteerableSnakeView implements Runnable {

	private double a20;
	private double a22;
	private double a40;
	private double a42;
	private double a44;
	private double a11;
	private double a31;
	private double a33;
	private double a51;
	private double a53;
	private static double[] gx;
	private static double[] gy;
	private static double[] gxx;
	private static double[] gxy;
	private static double[] gyy;
	private static double[] gxxx;
	private static double[] gxxy;
	private static double[] gxyy;
	private static double[] gyyy;
	private static double[] gxxxx;
	private static double[] gxxxy;
	private static double[] gxxyy;
	private static double[] gxyyy;
	private static double[] gyyyy;
	private static double[] gxxxxx;
	private static double[] gxxxxy;
	private static double[] gxxxyy;
	private static double[] gxxyyy;
	private static double[] gxyyyy;
	private static double[] gyyyyy;

	public int order;

	public double sigma, sigma2;

	public int size, nx, ny;

	final InteractiveMethods parent;

	final RandomAccessibleInterval<FloatType> CurrentView;

	public double[] response;

	public double[] orientation;

	private boolean stop = false;

	public double[] input;

	public SteerableSnakeView(final InteractiveMethods parent, final RandomAccessibleInterval<FloatType> CurrentView,
			double sigma, int order) {

		this.parent = parent;

		this.CurrentView = CurrentView;

		this.sigma = sigma;
		this.nx = (int) CurrentView.dimension(0);
		this.ny = (int) CurrentView.dimension(1);
		sigma2 = (sigma * sigma);
		this.order = order;

		size = nx * ny;

		response = new double[size];
		orientation = new double[size];

		input = convertFloatsToDoubles(((float[]) parent.imp.getProcessor().getPixels()));

	}

	public void run() {

		int a = 1;

		switch (order) {
		case 1:
			a11 = (a * -0.797884560802865D);
			filterM1();
			break;
		case 2:
			a20 = (a * 0.16286750396763996D * sigma);
			a22 = (a * -0.4886025119029199D * sigma);
			filterM2xx();

			break;
		case 3:
			a11 = (a * -0.966D);
			a31 = (a * -0.256D * sigma2);
			a33 = (a * 0.0D * sigma2);
			filterM3();
			break;
		case 4:
			a20 = (a * 0.059D * sigma);
			a22 = (a * -0.204D * sigma);
			a40 = (a * 0.024D * sigma * sigma2);
			a42 = (a * -0.194D * sigma * sigma2);
			a44 = (a * 0.063D * sigma * sigma2);
			filterM4();
			break;
		case 5:
			a11 = (a * -1.1215D);
			a31 = (a * -0.5576D * sigma2);
			a33 = (a * -0.018D * sigma2);
			a51 = (a * -0.0415D * sigma2 * sigma2);
			a53 = (a * -0.0038D * sigma2 * sigma2);
			filterM5();
			break;
		}

	}

	public double[] getResponse() {
		return response;
	}

	public double[] getOrientation() {
		return orientation;
	}

	  public double[] computeRotations(int nIncrements)
	  {
	    double[] rotations = new double[size * nIncrements];
	    double radiantStep = 6.283185307179586D / nIncrements;
	    
	    switch (order) {
	    case 1: 
	      for (int k = 0; k < nIncrements / 2; k++) {
	        for (int i = 0; i < size; i++) {
	          rotations[(i + k * size)] = pointRespM1(i, k * radiantStep);
	          rotations[(i + (k + nIncrements / 2) * size)] = 
	            (-rotations[(i + k * size)]);
	        }
	      }
	      break;
	    case 2: 
	      for (int k = 0; k < nIncrements / 2; k++) {
	        for (int i = 0; i < size; i++) {
	          rotations[(i + k * size)] = pointRespM2(i, k * radiantStep);
	          rotations[(i + (k + nIncrements / 2) * size)] = rotations[
	            (i + k * size)];
	        }
	      }
	      break;
	    case 3: 
	      for (int k = 0; k < nIncrements / 2; k++) {
	        for (int i = 0; i < size; i++) {
	          rotations[(i + k * size)] = pointRespM3(i, k * radiantStep);
	          rotations[(i + (k + nIncrements / 2) * size)] = 
	            (-rotations[(i + k * size)]);
	        }
	      }
	      break;
	    case 4: 
	      for (int k = 0; k < nIncrements / 2; k++) {
	        for (int i = 0; i < size; i++) {
	          rotations[(i + k * size)] = pointRespM4(i, k * radiantStep);
	          rotations[(i + (k + nIncrements / 2) * size)] = rotations[
	            (i + k * size)];
	        }
	      }
	      break;
	    case 5: 
	      int k = 0;
	      for (;;) { for (int i = 0; i < size; i++) {
	          rotations[(i + k * size)] = pointRespM5(i, k * radiantStep);
	          rotations[(i + (k + nIncrements / 2) * size)] = 
	            (-rotations[(i + k * size)]);
	        }
	        k++; if (k >= nIncrements / 2) {
	          break;
	        }
	      }
	    }
	    
	    




	    return rotations;
	  }
	  
	  

	  public int getWidth() {
	    return nx;
	  }
	  
	  public int getHeight() {
	    return ny;
	  }
	  
	  public String getOrder()
	  {
	    switch (order) {
	    case 1: 
	      return "1st";
	    case 2: 
	      return "2nd";
	    case 3: 
	      return "3rd";
	    case 4: 
	    case 5: 
	      return String.valueOf(order) + "th";
	    }
	    return "";
	  }
	  

	  public void showColorOrientation(String title)
	  {
	    byte[] h = new byte[size];
	    byte[] s = new byte[size];
	    byte[] b = new byte[size];
	    double min = Double.MAX_VALUE;
	    double max = -1.7976931348623157E308D;
	    for (int i = 0; i < size; i++) {
	      if (response[i] > max) {
	        max = response[i];
	      }
	      if (response[i] < min) {
	        min = response[i];
	      }
	    }
	    
	    if (order % 2 != 0) {
	      for (int i = 0; i < size; i++) {
	        h[i] = ((byte)(int)((orientation[i] / 6.283185307179586D + 0.5D) * 255.0D));
	        s[i] = -1;
	        b[i] = ((byte)(int)((response[i] - min) / (max - min) * 255.0D));
	      }
	    } else {
	      for (int i = 0; i < size; i++) {
	        h[i] = ((byte)(int)((orientation[i] / 3.141592653589793D + 0.5D) * 255.0D));
	        s[i] = -1;
	        b[i] = ((byte)(int)((response[i] - min) / (max - min) * 255.0D));
	      }
	    }
	    ColorProcessor cp = new ColorProcessor(nx, ny);
	    cp.setHSB(h, s, b);
	    ImagePlus ip = new ImagePlus(title, cp);
	    ip.show();
	  }
	  public void showNMS()
	  {
	    IJ.showStatus("Computing non-maximum suppression");
	    computeNMS();
	  }
	  

	  public void showRotations(int nIncrements)
	  {
	    IJ.showStatus("Computing " + getOrder() + " order filter iterations");
	   
	      computeRotations(nIncrements);
	  }
	  



	  public double[] computeNMS()
	  {
	    double[] output = new double[size];
	    


	    int idx = 0;
	    Interpolator interpolator = new Interpolator(response, nx, ny, "linear");
	    for (int y = 0; y < ny; y++) {
	      for (int x = 0; x < nx; x++) {
	        double angle = orientation[idx];
	        double ux = -Math.sin(angle);
	        double uy = Math.cos(angle);
	        double GA1 = interpolator.getValue(x + ux, y + uy);
	        double GA2 = interpolator.getValue(x - ux, y - uy);
	        double GA = response[idx];
	        if ((GA < GA1) || (GA < GA2)) {
	          output[idx] = 0.0D;
	        } else {
	          output[idx] = GA;
	        }
	        idx++;
	      }
	    }
	    return output;
	  }
	  
	private void filterM5() {
		filterM1();
		double[] initialAngle = getOrientation();

		double a1 = 2.0D * a31 - 3.0D * a33;
		double a2 = 4.0D * a51 - 3.0D * a53;
		double a3 = 6.0D * a33 - 7.0D * a31;
		double a4 = 12.0D * a51 - 17.0D * a53;
		double a5 = 6.0D * a53 - 13.0D * a51;
		double a6 = 3.0D * a33 - 5.0D * a31;
		double a7 = a31 - 3.0D * a33;
		double a8 = 30.0D * a53 - 34.0D * a51;
		double a53x2 = 2.0D * a53;
		double a11x2 = 2.0D * a11;
		double[] quinticC = new double[6];
		double[] quarticC = new double[5];
		double[] cubicC = new double[4];

		computeBaseTemplates(input, nx, ny, order, sigma);
		for (int i = 0; (i < size) && (!stop); i++) {
			quinticC[0] = (-a11 * gx[i] + a1 * gxyy[i] - a31 * gxxx[i] - a51 * gxxxxx[i] + a2 * gxxxyy[i]
					+ a53x2 * gxyyyy[i]);
			quinticC[1] = (-a11 * gy[i] + a3 * gxxy[i] + a5 * gxxxxy[i] + a1 * gyyy[i] + a4 * gxxyyy[i]
					+ a53x2 * gyyyyy[i]);
			quinticC[2] = (-a11x2 * gx[i] + a7 * gxxx[i] + a2 * gxxxxx[i] + a6 * gxyy[i] + a8 * gxxxyy[i]
					+ a4 * gxyyyy[i]);
			quinticC[3] = (-a11x2 * gy[i] + a6 * gxxy[i] + a4 * gxxxxy[i] + a7 * gyyy[i] + a8 * gxxyyy[i]
					+ a2 * gyyyyy[i]);
			quinticC[4] = (-a11 * gx[i] + a1 * gxxx[i] + a53x2 * gxxxxx[i] + a3 * gxyy[i] + a4 * gxxxyy[i]
					+ a5 * gxyyyy[i]);
			quinticC[5] = (-a11 * gy[i] + a1 * gxxy[i] - a31 * gyyy[i] + a53x2 * gxxxxy[i] + a2 * gxxyyy[i]
					- a51 * gyyyyy[i]);

			quinticC[5] = approxZero(quinticC[5], 1.0E-13D);
			quinticC[4] = approxZero(quinticC[4], 1.0E-13D);
			quinticC[3] = approxZero(quinticC[3], 1.0E-13D);
			quinticC[2] = approxZero(quinticC[2], 1.0E-13D);
			quinticC[1] = approxZero(quinticC[1], 1.0E-13D);
			quinticC[0] = approxZero(quinticC[0], 1.0E-13D);

			if (quinticC[5] == 0.0D) {
				ArrayList<Pair<Integer, Double>> xRoots = new ArrayList<Pair<Integer, Double>>();
				
				if (quinticC[4] == 0.0D) {
					if (quinticC[3] == 0.0D) {
						if (quinticC[2] == 0.0D) {
							if (quinticC[1] == 0.0D) {
								xRoots.add(new ValuePair<Integer, Double>(1, 0.0));
								
							} else {
								xRoots.add(new ValuePair<Integer, Double>(1, -quinticC[0] / quinticC[1]));
							}
						} else {
							
							xRoots = numericalSolvers.Solvers.SolveQuadratic(new double[] {quinticC[0] , quinticC[1], quinticC[2]});
						}
					} else {
						xRoots = numericalSolvers.Solvers.SolveCubic(new double[] {quinticC[0] , quinticC[1], quinticC[2], quinticC[3] });
						
					}
				} else {
					
					xRoots = numericalSolvers.Solvers.SolveQuartic(new double[] {quinticC[0] , quinticC[1], quinticC[2], quinticC[3],quinticC[4] });
					
				}

				int N = xRoots.size();

				if (N != 0) {
					double[] tRoots = new double[2 * N + 4];
					for (int k = 0; k < N; k++) {
						tRoots[k] = Math.atan(xRoots.get(k).getB());
						tRoots[(k + N)] = opposite(tRoots[k]);
					}
					tRoots[(2 * N)] = 0.0D;
					tRoots[(2 * N + 1)] = 1.5707963267948966D;
					tRoots[(2 * N + 2)] = 3.141592653589793D;
					tRoots[(2 * N + 3)] = -1.5707963267948966D;

					response[i] = pointRespM5(i, tRoots[0]);
					orientation[i] = tRoots[0];
					for (int k = 1; k < tRoots.length; k++) {
						double temp = pointRespM5(i, tRoots[k]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[k];
						}
					}
				} else {
					double[] tRoots = new double[4];
					tRoots[0] = 0.0D;
					tRoots[1] = 1.5707963267948966D;
					tRoots[2] = 3.141592653589793D;
					tRoots[3] = -1.5707963267948966D;
					response[i] = pointRespM5(i, tRoots[0]);
					orientation[i] = tRoots[0];
					for (int k = 1; k < tRoots.length; k++) {
						double temp = pointRespM5(i, tRoots[k]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[k];
						}
					}
				}
			} else {
				ArrayList<Pair<Integer, Double>> rootsA = new ArrayList<Pair<Integer, Double>>();
				double theta = initialAngle[i];
				double x0 = Math.tan(theta);
				double[] f0 = evalPolyD(quinticC, x0);
				int N;
				double[] roots;
				if (quinticC[0] == 0.0D) {
					
					ArrayList<Pair<Integer, Double>>	quarticRoots = numericalSolvers.Solvers.SolveQuartic(new double[] { quinticC[1], quinticC[2] , quinticC[3] , quinticC[4], quinticC[5]});
					
					 N = quarticRoots.size();
					roots = new double[N + 1];
					roots[0] = 0.0D;
					for (int k = 1; k <= N; k++) {
						roots[k] = quarticRoots.get(k - 1).getB();
					}
					N++;
				} else if (f0[0] == 0.0D) {
					quarticC = divPolyByRoot(quinticC, x0);
					ArrayList<Pair<Integer, Double>>	quarticRoots = numericalSolvers.Solvers.SolveQuartic(new double[] {quarticC[0], quarticC[1], quarticC[2], quarticC[3], quarticC[4]});
					
					
					N = quarticRoots.size();
					 roots = new double[N + 1];
					roots[0] = x0;
					for (int k = 1; k <= N; k++) {
						roots[k] = quarticRoots.get(k - 1).getB();
					}
					N++;

				} else {

					if (Math.abs(x0) >= 100.0D) {
						x0 = 0.0D;
					}
					double[] croot = laguerre(quinticC, x0);

					croot[0] = approxZero(croot[0], 1.0E-15D);
					croot[1] = approxZero(croot[1], 1.0E-15D);

					if (croot[1] == 0.0D) {
						quarticC = divPolyByRoot(quinticC, croot[0]);
						
						ArrayList<Pair<Integer, Double>>	quarticRoots = numericalSolvers.Solvers.SolveQuartic(new double[] {quarticC[0], quarticC[1], quarticC[2], quarticC[3], quarticC[4]});
						
					
						N = quarticRoots.size();
						roots = new double[N + 1];
						roots[0] = 0.0D;
				          for (int k = 1; k <= N; k++) {
				            roots[k] = quarticRoots.get(k - 1).getB();
				          }
						N++;
					} else {
						cubicC = divPolyByConjRoots(quinticC, croot[0], croot[1]);
						rootsA = numericalSolvers.Solvers.SolveCubic(new double[] {cubicC[0],cubicC[1], cubicC[2], cubicC[3] });
						N = rootsA.size();
						roots = new double[N + 1];
						for (int k = 0; k < rootsA.size(); ++k) {
							
							roots[k] = rootsA.get(k).getB();
						}
						
						N = roots.length;
					}
				}

				double[] tRoots = new double[2 * N];
				for (int k = 0; k < N; k++) {
					tRoots[k] = Math.atan(roots[k]);
					tRoots[(k + N)] = opposite(tRoots[k]);
				}

				response[i] = pointRespM5(i, tRoots[0]);
				orientation[i] = tRoots[0];
				for (int k = 1; k < tRoots.length; k++) {
					double temp = pointRespM5(i, tRoots[k]);
					if (temp > response[i]) {
						response[i] = temp;
						orientation[i] = tRoots[k];
					}
				}
			}
		}
	}
	
	private void structureTensor() {
		double[] eigenValues = new double[2];

		double[] eV = new double[2];

		IJ.showStatus("Computing optimal orientation");
		int iz = 0;

		int wWidth = (int) (4.0D * sigma);
		int kLength = wWidth + 1;
		double[] g = new double[kLength];
		double[] aKernel = new double[kLength];

		double[] gx = new double[kLength];
		double[] gy = new double[kLength];
		double[] gx2 = new double[kLength];
		double[] gxgy = new double[kLength];
		double[] gy2 = new double[kLength];
		double sigma2 = sigma * sigma;
		double sigma4 = sigma2 * sigma2;

		for (int i = 0; i < kLength; i++) {
			g[i] = Math.exp(-(i * i) / (2.0D * sigma2));
		}

		double d = 6.283185307179586D * sigma4;
		for (int i = 0; i < kLength; i++) {
			aKernel[i] = (-i * g[i] / d);
		}

		gx = Convolvers.convolveOddX(input, aKernel, nx, ny);
		gx = Convolvers.convolveEvenY(gx, g, nx, ny);
		gy = Convolvers.convolveOddY(input, aKernel, nx, ny);
		gy = Convolvers.convolveEvenX(gy, g, nx, ny);

		for (int i = 0; i < size; i++) {
			gx[i] *= gx[i];
			gx[i] *= gy[i];
			gy[i] *= gy[i];
		}

		gx2 = Convolvers.convolveEvenX(gx2, g, nx, ny);
		gx2 = Convolvers.convolveEvenY(gx2, g, nx, ny);
		gxgy = Convolvers.convolveEvenX(gxgy, g, nx, ny);
		gxgy = Convolvers.convolveEvenY(gxgy, g, nx, ny);
		gy2 = Convolvers.convolveEvenX(gy2, g, nx, ny);
		gy2 = Convolvers.convolveEvenY(gy2, g, nx, ny);

		for (int i = 0; (i < size) && (!stop); i++) {

			gx2[i] = approxZero(gx2[i], 1.0E-13D);
			gxgy[i] = approxZero(gxgy[i], 1.0E-13D);
			gy2[i] = approxZero(gy2[i], 1.0E-13D);

			if (gxgy[i] == 0.0D) {
				eigenValues[0] = gx2[i];
				eigenValues[1] = gy2[i];
			} else {
				
				
				ArrayList<Pair<Integer, Double>>	quarticRoots = numericalSolvers.Solvers.SolveQuartic(new double[] {gx2[i] * gy2[i] - gxgy[i] * gxgy[i],-gx2[i] - gy2[i], 1, 0 ,0   });
				
				eigenValues = new double[] {quarticRoots.get(0).getB(), quarticRoots.get(1).getB()};
			}

			double minEigenvalue = eigenValues[0];
			if (eigenValues[1] < minEigenvalue) {
				minEigenvalue = eigenValues[1];
			}

			if (gxgy[i] == 0.0D) {
				if (minEigenvalue == gx2[i]) {
					eV[0] = 1.0D;
					eV[1] = 0.0D;
					orientation[iz] = 0.0D;
				} else {
					eV[0] = 0.0D;
					eV[1] = 1.0D;
					orientation[iz] = 1.5707963267948966D;
				}
			} else {
				eV[0] = 1.0D;
				eV[1] = ((minEigenvalue - gx2[i]) / gxgy[i]);
				normalize(eV);
				orientation[iz] = Math.atan2(eV[1], eV[0]);
			}

			response[iz] = (gx2[i] + gy2[i]);
			iz++;
		}
	}
	
	private static double[] complex(double paramDouble1, double paramDouble2)
	  {
	    double[] arrayOfDouble = { paramDouble1, paramDouble2 };
	    return arrayOfDouble;
	  }
	  
	  private static double[] cadd(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    double[] arrayOfDouble = { paramArrayOfDouble1[0] + paramArrayOfDouble2[0], paramArrayOfDouble1[1] + paramArrayOfDouble2[1] };
	    return arrayOfDouble;
	  }
	  
	  private static double[] csub(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    double[] arrayOfDouble = { paramArrayOfDouble1[0] - paramArrayOfDouble2[0], paramArrayOfDouble1[1] - paramArrayOfDouble2[1] };
	    return arrayOfDouble;
	  }
	  
	  private static double cabs(double[] paramArrayOfDouble)
	  {
	    return Math.sqrt(paramArrayOfDouble[0] * paramArrayOfDouble[0] + paramArrayOfDouble[1] * paramArrayOfDouble[1]);
	  }
	  
	  private static double[] rcmul(double paramDouble, double[] paramArrayOfDouble)
	  {
	    double[] arrayOfDouble = { paramDouble * paramArrayOfDouble[0], paramDouble * paramArrayOfDouble[1] };
	    return arrayOfDouble;
	  }
	  
	  private static double[] cmul(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    double[] arrayOfDouble = { paramArrayOfDouble1[0] * paramArrayOfDouble2[0] - paramArrayOfDouble1[1] * paramArrayOfDouble2[1], paramArrayOfDouble1[0] * paramArrayOfDouble2[1] + paramArrayOfDouble2[0] * paramArrayOfDouble1[1] };
	    return arrayOfDouble;
	  }
	  
	  private static double[] cdiv(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    double d = paramArrayOfDouble2[0] * paramArrayOfDouble2[0] + paramArrayOfDouble2[1] * paramArrayOfDouble2[1];
	    double[] arrayOfDouble = { (paramArrayOfDouble1[0] * paramArrayOfDouble2[0] + paramArrayOfDouble1[1] * paramArrayOfDouble2[1]) / d, (paramArrayOfDouble2[0] * paramArrayOfDouble1[1] - paramArrayOfDouble1[0] * paramArrayOfDouble2[1]) / d };
	    return arrayOfDouble;
	  }
	  
	  private static double[] csqrt(double[] paramArrayOfDouble)
	  {
	    double d1 = cabs(paramArrayOfDouble);
	    double d2 = Math.sqrt(2.0D);
	    double[] arrayOfDouble = { Math.sqrt(d1 + paramArrayOfDouble[0]) / d2, csign(paramArrayOfDouble[1]) * Math.sqrt(d1 - paramArrayOfDouble[0]) / d2 };
	    return arrayOfDouble;
	  }
	  
	  private static double csign(double paramDouble)
	  {
	    if (paramDouble >= 0.0D) {
	      return 1.0D;
	    }
	    return -1.0D;
	  }
	  
	  public static double mean(double[] paramArrayOfDouble)
	  {
	    return sum(paramArrayOfDouble) / paramArrayOfDouble.length;
	  }
	  
	  public static double mean(double[][] paramArrayOfDouble)
	  {
	    return sum(paramArrayOfDouble) / (paramArrayOfDouble.length * paramArrayOfDouble[0].length);
	  }
	  
	  public static double sum(double[] paramArrayOfDouble)
	  {
	    int i = paramArrayOfDouble.length;
	    double d = 0.0D;
	    for (int j = 0; j < i; j++) {
	      d += paramArrayOfDouble[j];
	    }
	    return d;
	  }
	  
	  public static double sum(double[][] paramArrayOfDouble)
	  {
	    int i = paramArrayOfDouble.length;
	    int j = paramArrayOfDouble[0].length;
	    double d = 0.0D;
	    for (int k = 0; k < i; k++) {
	      for (int m = 0; m < j; m++) {
	        d += paramArrayOfDouble[k][m];
	      }
	    }
	    return d;
	  }
	  
	  public static double[] add(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    double[] arrayOfDouble = new double[i];
	    for (int j = 0; j < i; j++) {
	      paramArrayOfDouble1[j] += paramArrayOfDouble2[j];
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] add(double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    int j = paramArrayOfDouble1[0].length;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int k = 0; k < i; k++) {
	      for (int m = 0; m < j; m++) {
	        paramArrayOfDouble1[k][m] += paramArrayOfDouble2[k][m];
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] subtract(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    double[] arrayOfDouble = new double[i];
	    for (int j = 0; j < i; j++) {
	      paramArrayOfDouble1[j] -= paramArrayOfDouble2[j];
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] subtract(double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    int j = paramArrayOfDouble1[0].length;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int k = 0; k < i; k++) {
	      for (int m = 0; m < j; m++) {
	        paramArrayOfDouble1[k][m] -= paramArrayOfDouble2[k][m];
	      }
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] mutiply(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    double[] arrayOfDouble = new double[i];
	    for (int j = 0; j < i; j++) {
	      paramArrayOfDouble1[j] *= paramArrayOfDouble2[j];
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[][] multiply(double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
	  {
	    int i = paramArrayOfDouble1.length;
	    int j = paramArrayOfDouble1[0].length;
	    double[][] arrayOfDouble = new double[i][j];
	    for (int k = 0; k < i; k++) {
	      for (int m = 0; m < j; m++) {
	        paramArrayOfDouble1[k][m] *= paramArrayOfDouble2[k][m];
	      }
	    }
	    return arrayOfDouble;
	  }
	
	
	public static double[] laguerre(double[] paramArrayOfDouble, double paramDouble)
	  {
	    int i = paramArrayOfDouble.length - 1;
	    double[] arrayOfDouble1 = complex(paramDouble, 0.0D);
	    double[] arrayOfDouble12 = { 0.0D, 0.5D, 0.25D, 0.75D, 0.125D, 0.375D, 0.625D, 0.875D, 1.0D };
	    int j = 10;
	    int k = 30;
	    double d5 = 1.0E-15D;
	    int[] arrayOfInt = new int[1];
	    arrayOfInt[0] = -2;
	    for (int m = 1; m <= k; m++)
	    {
	      double[] arrayOfDouble2 = complex(paramArrayOfDouble[i], 0.0D);
	      double[] arrayOfDouble3 = complex(0.0D, 0.0D);
	      double[] arrayOfDouble4 = complex(0.0D, 0.0D);
	      double d1 = cabs(arrayOfDouble1);
	      double d2 = cabs(arrayOfDouble2);
	      for (int n = i - 1; n >= 0; n--)
	      {
	        arrayOfDouble4 = cadd(cmul(arrayOfDouble4, arrayOfDouble1), arrayOfDouble3);
	        arrayOfDouble3 = cadd(cmul(arrayOfDouble3, arrayOfDouble1), arrayOfDouble2);
	        arrayOfDouble2 = cadd(cmul(arrayOfDouble2, arrayOfDouble1), complex(paramArrayOfDouble[n], 0.0D));
	        d2 = cabs(arrayOfDouble2) + d1 * d2;
	      }
	      if (cabs(arrayOfDouble2) <= d2 * d5)
	      {
	        arrayOfInt[0] = 2;
	        break;
	      }
	      double[] arrayOfDouble5 = cdiv(arrayOfDouble3, arrayOfDouble2);
	      double[] arrayOfDouble6 = cmul(arrayOfDouble5, arrayOfDouble5);
	      double[] arrayOfDouble7 = csub(arrayOfDouble6, cdiv(arrayOfDouble4, arrayOfDouble2));
	      double[] arrayOfDouble11 = rcmul(i - 1, csub(rcmul(i, arrayOfDouble7), arrayOfDouble6));
	      double[] arrayOfDouble8 = csqrt(arrayOfDouble11);
	      Object localObject = cadd(arrayOfDouble5, arrayOfDouble8);
	      double[] arrayOfDouble9 = csub(arrayOfDouble5, arrayOfDouble8);
	      double d3 = cabs((double[])localObject);
	      double d4 = cabs(arrayOfDouble9);
	      if (d4 > d3)
	      {
	        localObject = arrayOfDouble9;
	        d3 = d4;
	      }
	      double[] arrayOfDouble10;
	      if (d3 > 0.0D)
	      {
	        arrayOfDouble10 = cdiv(complex(i, 0.0D), (double[])localObject);
	      }
	      else
	      {
	        arrayOfDouble10 = rcmul(1.0D + d1, complex(Math.cos(m), Math.sin(m)));
	        arrayOfDouble10 = complex(1.0D, 0.0D);
	      }
	      if (m % j != 0) {
	        arrayOfDouble1 = csub(arrayOfDouble1, arrayOfDouble10);
	      } else {
	        arrayOfDouble1 = csub(arrayOfDouble1, rcmul(arrayOfDouble12[(m / j)], arrayOfDouble10));
	      }
	    }
	    return arrayOfDouble1;
	  }
	  
	
	public static double[] divPolyByRoot(double[] paramArrayOfDouble, double paramDouble)
	  {
	    int i = paramArrayOfDouble.length;
	    double[] arrayOfDouble = new double[i - 1];
	    double d = paramArrayOfDouble[(i - 1)];
	    for (int j = i - 2; j >= 0; j--)
	    {
	      arrayOfDouble[j] = d;
	      d = paramArrayOfDouble[j] + d * paramDouble;
	    }
	    return arrayOfDouble;
	  }
	  
	  public static double[] divPolyByConjRoots(double[] paramArrayOfDouble, double paramDouble1, double paramDouble2)
	  {
	    double d1 = 2.0D * paramDouble1;
	    double d2 = paramDouble1 * paramDouble1 + paramDouble2 * paramDouble2;
	    int i = paramArrayOfDouble.length - 2;
	    double[] arrayOfDouble = new double[i];
	    arrayOfDouble[(i - 1)] = paramArrayOfDouble[(i + 1)];
	    arrayOfDouble[(i - 2)] = (paramArrayOfDouble[i] + d1 * arrayOfDouble[(i - 1)]);
	    for (int j = i - 1; j >= 2; j--) {
	      arrayOfDouble[(j - 2)] = (paramArrayOfDouble[j] + d1 * arrayOfDouble[(j - 1)] - d2 * arrayOfDouble[j]);
	    }
	    return arrayOfDouble;
	  }
	public static double[] evalPolyD(double[] paramArrayOfDouble, double paramDouble)
	  {
	    int i = paramArrayOfDouble.length;
	    double[] arrayOfDouble = new double[2];
	    arrayOfDouble[0] = paramArrayOfDouble[(i - 1)];
	    arrayOfDouble[1] = 0.0D;
	    for (int j = i - 2; j >= 0; j--)
	    {
	      arrayOfDouble[1] = (arrayOfDouble[1] * paramDouble + arrayOfDouble[0]);
	      arrayOfDouble[0] = (arrayOfDouble[0] * paramDouble + paramArrayOfDouble[j]);
	    }
	    return arrayOfDouble;
	  }
	private void filterM4() {
		double tolerance = 1.0E-12D;

		IJ.showStatus("Computing optimal orientation");

		double a1 = 2.0D * a44 - a42;
		double a2 = 2.0D * a40 - a42;
		double a3 = a22 - a20;
		double a4 = 6.0D * (a44 - a42 + a40);

		computeBaseTemplates(input, nx, ny, order, sigma);
		for (int i = 0; (i < size) && (!stop); i++) {
			double g1 = a3 * gxy[i];
			double g2 = a3 * (gxx[i] - gyy[i]);
			double g3 = a4 * gxxyy[i];

			double A = a1 * gxxxy[i] - a2 * gxyyy[i] + g1;
			double B = a1 * gxxxx[i] + a2 * gyyyy[i] - g3 + g2;
			double C = a4 * (gxyyy[i] - gxxxy[i]);
			double D = -a1 * gyyyy[i] - a2 * gxxxx[i] + g3 + g2;
			double E = a2 * gxxxy[i] - a1 * gxyyy[i] - g1;

			A = approxZero(A, tolerance);
			C = approxZero(C, tolerance);
			E = approxZero(E, tolerance);

			if (A == 0.0D) {
				if (B == 0.0D) {
					if (C == 0.0D) {
						if (D == 0.0D) {
							response[i] = pointRespM4(i, 0.0D);
							orientation[i] = 0.0D;
						} else {
							orientation[i] = Math.atan(-E / D);
							response[i] = pointRespM4(i, orientation[i]);
						}
					} else {

						ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
								.SolveQuadratic(new double[] { E, D, C });

						int N = xRoots.size();
						if (N == 0) {
							response[i] = pointRespM4(i, 0.0D);
							orientation[i] = 0.0D;
						} else {
							double[] tRoots = new double[2];
							tRoots[0] = Math.atan(xRoots.get(0).getB());
							tRoots[1] = Math.atan(xRoots.get(1).getB());
							response[i] = pointRespM4(i, tRoots[0]);
							orientation[i] = tRoots[0];
							double temp = pointRespM4(i, tRoots[1]);
							if (temp > response[i]) {
								response[i] = temp;
								orientation[i] = tRoots[1];
							}
						}
					}
				} else if ((C == 0.0D) && (E == 0.0D)) {
					double deltaQ = -D / B;
					if (deltaQ >= 0.0D) {
						double[] tRoots = new double[4];
						tRoots[0] = Math.atan(Math.sqrt(deltaQ));
						tRoots[1] = Math.atan(-Math.sqrt(deltaQ));
						tRoots[2] = 0.0D;
						tRoots[3] = 1.5707963267948966D;

						response[i] = pointRespM4(i, tRoots[0]);
						orientation[i] = tRoots[0];
						for (int k = 1; k < 4; k++) {
							double temp = pointRespM4(i, tRoots[k]);
							if (temp > response[i]) {
								response[i] = temp;
								orientation[i] = tRoots[k];
							}
						}
					} else {
						double[] tRoots = new double[2];
						tRoots[0] = 0.0D;
						tRoots[1] = 1.5707963267948966D;

						response[i] = pointRespM4(i, 0.0D);
						orientation[i] = 0.0D;
						double temp = pointRespM4(i, 1.5707963267948966D);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = 1.5707963267948966D;
						}
					}
				} else {

					ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
							.SolveCubic(new double[] { E, D, C, B });

					int N = xRoots.size();
					double[] tRoots = new double[N];
					for (int k = 0; k < N; k++) {
						tRoots[k] = Math.atan(xRoots.get(k).getB());
					}
					response[i] = pointRespM4(i, tRoots[0]);
					orientation[i] = tRoots[0];
					for (int k = 1; k < N; k++) {
						double temp = pointRespM4(i, tRoots[k]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[k];
						}
					}
				}
			} else {

				ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
						.SolveQuartic(new double[] { E, D, C, B, A });

				int N = xRoots.size();
				if (N == 0) {
					orientation[i] = 0.0D;
					response[i] = pointRespM4(i, 0.0D);
				} else {
					double[] tRoots = new double[N];
					for (int k = 0; k < N; k++) {
						tRoots[k] = Math.atan(xRoots.get(k).getB());
					}
					response[i] = pointRespM4(i, tRoots[0]);
					orientation[i] = tRoots[0];
					for (int k = 1; k < N; k++) {
						double temp = pointRespM4(i, tRoots[k]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[k];
						}
					}
				}
			}
		}
	}

	private void filterM3() {
		double a1 = 2.0D * a31 - 3.0D * a33;
		double a2 = 6.0D * a33 - 7.0D * a31;

		IJ.showStatus("Computing optimal orientation");

		computeBaseTemplates(input, nx, ny, order, sigma);
		for (int i = 0; (i < size) && (!stop); i++) {
			double g1 = -a11 * gy[i];
			double g2 = -a11 * gx[i];

			double A = g1 - a31 * gyyy[i] + a1 * gxxy[i];
			double B = g2 + a2 * gxyy[i] + a1 * gxxx[i];
			double C = g1 + a2 * gxxy[i] + a1 * gyyy[i];
			double D = g2 - a31 * gxxx[i] + a1 * gxyy[i];

			A = approxZero(A, 1.0E-13D);
			B = approxZero(B, 1.0E-13D);
			C = approxZero(C, 1.0E-13D);
			D = approxZero(D, 1.0E-13D);

			if (A == 0.0D) {
				if (B == 0.0D) {
					if (C == 0.0D) {
						orientation[i] = 0.0D;
						response[i] = pointRespM3(i, 0.0D);
					} else if (D == 0.0D) {
						double[] tRoots = new double[4];
						tRoots[0] = -1.5707963267948966D;
						tRoots[1] = 0.0D;
						tRoots[2] = 1.5707963267948966D;
						tRoots[3] = 3.141592653589793D;
						response[i] = pointRespM3(i, tRoots[0]);
						orientation[i] = tRoots[0];
						for (int k = 1; k < 4; k++) {
							double temp = pointRespM3(i, tRoots[k]);
							if (temp > response[i]) {
								response[i] = temp;
								orientation[i] = tRoots[k];
							}
						}
					} else {
						double[] tRoots = new double[2];
						tRoots[0] = Math.atan(-D / C);
						tRoots[1] = opposite(tRoots[0]);
						response[i] = pointRespM3(i, tRoots[0]);
						orientation[i] = tRoots[0];
						double temp = pointRespM3(i, tRoots[1]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[1];
						}
					}
				} else {

					ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
							.SolveQuadratic(new double[] { D, C, B });

					double[] tRoots = new double[4];
					if ((xRoots.size() == 0) || (xRoots.get(0).getB() == 0.0D)
							|| (xRoots.get(0).getB() == -xRoots.get(1).getB())) {
						tRoots[0] = -1.5707963267948966D;
						tRoots[1] = 0.0D;
						tRoots[2] = 1.5707963267948966D;
						tRoots[3] = 3.141592653589793D;
					} else {
						tRoots[0] = Math.atan(xRoots.get(0).getB());
						tRoots[1] = Math.atan(xRoots.get(1).getB());
						tRoots[2] = opposite(tRoots[0]);
						tRoots[3] = opposite(tRoots[1]);
					}
					response[i] = pointRespM3(i, tRoots[0]);
					orientation[i] = tRoots[0];
					for (int k = 1; k < 4; k++) {
						double temp = pointRespM3(i, tRoots[k]);
						if (temp > response[i]) {
							response[i] = temp;
							orientation[i] = tRoots[k];
						}
					}
				}
			} else {
				ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
						.SolveCubic(new double[] { D, C, B, A });
				double[] tRoots = new double[2 * xRoots.size()];
				double[] currentResponse = new double[tRoots.length];

				for (int k = 0; k < xRoots.size(); k++) {
					tRoots[k] = Math.atan(xRoots.get(k).getB());
					tRoots[(k + xRoots.size())] = opposite(tRoots[k]);
				}

				for (int k = 0; k < tRoots.length; k++) {
					currentResponse[k] = pointRespM3(i, tRoots[k]);
				}
				sort(currentResponse, tRoots);

				if (xRoots.size() == 3) {
					if (approxEqual(currentResponse[(tRoots.length - 1)], currentResponse[(tRoots.length - 2)],
							1.0E-6D)) {
						response[i] = currentResponse[(tRoots.length - 3)];
						orientation[i] = tRoots[(tRoots.length - 3)];
					} else {
						response[i] = currentResponse[(tRoots.length - 1)];
						orientation[i] = tRoots[(tRoots.length - 1)];
					}
				} else {
					response[i] = currentResponse[(tRoots.length - 1)];
					orientation[i] = tRoots[(tRoots.length - 1)];
				}
			}
		}
	}

	private void filterM1() {
		double[] tRoots = new double[2];

		IJ.showStatus("Computing optimal orientation");

		computeBaseTemplates(input, nx, ny, order, sigma);
		int i = 0;

		for (int iz = 0; (iz < size) && (!stop); iz++) {
			double gxi = gx[i];
			double gyi = gy[i];
			gxi = approxZero(gxi, 1.0E-13D);
			gyi = approxZero(gyi, 1.0E-13D);

			if ((gxi == 0.0D) && (gyi == 0.0D)) {
				response[iz] = 0.0D;
				orientation[iz] = 0.0D;
			} else {
				if (gyi == 0.0D) {
					tRoots[0] = 1.5707963267948966D;
					tRoots[1] = opposite(tRoots[0]);
				} else {
					tRoots[0] = Math.atan(-gxi / gyi);
					tRoots[1] = opposite(tRoots[0]);
				}
				orientation[iz] = tRoots[0];
				response[iz] = (Math.cos(tRoots[0]) * a11 * gyi - Math.sin(tRoots[0]) * a11 * gxi);
				double temp = Math.cos(tRoots[1]) * a11 * gyi - Math.sin(tRoots[1]) * a11 * gxi;
				if (temp > response[iz]) {
					response[iz] = temp;
					orientation[iz] = tRoots[1];
				}
			}
			i++;
		}
	}

	private void filterM2xx() {
		double[] tRoots = new double[2];

		IJ.showStatus("Computing optimal orientation");

		computeBaseTemplates(input, nx, ny, order, sigma);
		for (int i = 0; (i < size) && (!stop); i++) {
			double d = approxZero(gxx[i] - gyy[i], 1.0E-13D);
			double gxyi = approxZero(gxy[i], 1.0E-13D);

			if ((d == 0.0D) && (gxyi == 0.0D)) {
				response[i] = pointRespM2(i, 0.0D);
				orientation[i] = 0.0D;
			} else {
				if (gxyi == 0.0D) {
					tRoots[0] = 0.0D;
					tRoots[1] = -1.5707963267948966D;
				} else {
					tRoots[0] = (Math.atan(2.0D * gxyi / d) / 2.0D);
					tRoots[1] = complement(tRoots[0]);
				}

				orientation[i] = tRoots[0];
				response[i] = pointRespM2(i, tRoots[0]);
				double temp = pointRespM2(i, tRoots[1]);
				if (temp > response[i]) {
					response[i] = temp;
					orientation[i] = tRoots[1];
				}
			}
		}
	}

	private void filterM2() {
		double a = a20 - a22;

		IJ.showStatus("Computing optimal orientation");

		computeBaseTemplates(input, nx, ny, order, sigma);
		for (int i = 0; (i < size) && (!stop); i++) {
			double C = a * gxy[i];
			double B = a * (gyy[i] - gxx[i]);
			double A = -C;

			if (A == 0.0D) {
				if (B == 0.0D) {
					orientation[i] = 0.0D;
					response[i] = pointRespM2(i, 0.0D);
				} else if (C == 0.0D) {
					orientation[i] = 0.0D;
					response[i] = pointRespM2(i, 0.0D);
					double temp = pointRespM2(i, 1.5707963267948966D);
					if (temp > response[i]) {
						response[i] = temp;
						orientation[i] = 1.5707963267948966D;
					}
				} else {
					orientation[i] = Math.atan(-C / B);
					response[i] = pointRespM2(i, orientation[i]);
					double temp = pointRespM2(i, orientation[i] + 1.5707963267948966D);
					if (temp > response[i]) {
						response[i] = temp;
						orientation[i] += 1.5707963267948966D;
					}
				}
			} else {
				ArrayList<Pair<Integer, Double>> xRoots = numericalSolvers.Solvers
						.SolveQuadratic(new double[] { C, B, A });

				double[] tRoots = new double[2];
				tRoots[0] = Math.atan(xRoots.get(0).getB());
				tRoots[1] = Math.atan(xRoots.get(1).getB());
				response[i] = pointRespM2(i, tRoots[0]);
				orientation[i] = tRoots[0];
				double temp = pointRespM2(i, tRoots[1]);
				if (temp > response[i]) {
					response[i] = temp;
					orientation[i] = tRoots[1];
				}
			}
		}
	}

	public static void computeBaseTemplates(double[] input, final int order, int nx, int ny, final double sigma) {

		int wWidth = (int) (4.0D * sigma);
		int kLength = wWidth + 1;
		double[] aKernel = new double[kLength];
		double[] bKernel = new double[kLength];
		double[] g = new double[kLength];

		double sigma2 = sigma * sigma;
		double sigma4 = sigma2 * sigma2;
		double sigma6 = sigma4 * sigma2;
		double sigma8 = sigma4 * sigma4;
		double sigma10 = sigma6 * sigma4;
		double sigma12 = sigma8 * sigma4;

		for (int i = 0; i < kLength; i++) {
			g[i] = Math.exp(-(i * i) / (2.0D * sigma2));
		}

		if ((order == 1) || (order == 3) || (order == 5)) {
			double d = 6.283185307179586D * sigma4;
			for (int i = 0; i < kLength; i++) {
				aKernel[i] = (-i * g[i] / d);
			}

			gx = Convolvers.convolveOddX(input, aKernel, nx, ny);
			gx = Convolvers.convolveEvenY(gx, g, nx, ny);
			gy = Convolvers.convolveOddY(input, aKernel, nx, ny);
			gy = Convolvers.convolveEvenX(gy, g, nx, ny);

			if ((order == 3) || (order == 5)) {
				d = 6.283185307179586D * sigma8;
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = ((3.0D * i * sigma2 - i * i * i) * g[i] / d);
				}
				gxxx = Convolvers.convolveOddX(input, aKernel, nx, ny);
				gxxx = Convolvers.convolveEvenY(gxxx, g, nx, ny);
				gyyy = Convolvers.convolveOddY(input, aKernel, nx, ny);
				gyyy = Convolvers.convolveEvenX(gyyy, g, nx, ny);
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = ((sigma2 - i * i) * g[i] / d);
					bKernel[i] = (i * g[i]);
				}
				gxxy = Convolvers.convolveEvenX(input, aKernel, nx, ny);
				gxxy = Convolvers.convolveOddY(gxxy, bKernel, nx, ny);
				gxyy = Convolvers.convolveEvenY(input, aKernel, nx, ny);
				gxyy = Convolvers.convolveOddX(gxyy, bKernel, nx, ny);
			}

			if (order == 5) {
				d = 6.283185307179586D * sigma12;
				for (int i = 0; i < kLength; i++) {
					aKernel[i] =

							(-i * (i * i * i * i - 10.0D * i * i * sigma2 + 15.0D * sigma4) * g[i] / d);
				}
				gxxxxx = Convolvers.convolveOddX(input, aKernel, nx, ny);
				gxxxxx = Convolvers.convolveEvenY(gxxxxx, g, nx, ny);
				gyyyyy = Convolvers.convolveOddY(input, aKernel, nx, ny);
				gyyyyy = Convolvers.convolveEvenX(gyyyyy, g, nx, ny);
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = ((i * i * i * i - 6.0D * i * i * sigma2 + 3.0D * sigma4) * g[i] / d);
					bKernel[i] = (-i * g[i]);
				}
				gxxxxy = Convolvers.convolveEvenX(input, aKernel, nx, ny);
				gxxxxy = Convolvers.convolveOddY(gxxxxy, bKernel, nx, ny);
				gxyyyy = Convolvers.convolveEvenY(input, aKernel, nx, ny);
				gxyyyy = Convolvers.convolveOddX(gxyyyy, bKernel, nx, ny);
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = (i * (i * i - 3.0D * sigma2) * g[i] / d);
					bKernel[i] = ((sigma2 - i * i) * g[i]);
				}
				gxxxyy = Convolvers.convolveOddX(input, aKernel, nx, ny);
				gxxxyy = Convolvers.convolveEvenY(gxxxyy, bKernel, nx, ny);
				gxxyyy = Convolvers.convolveOddY(input, aKernel, nx, ny);
				gxxyyy = Convolvers.convolveEvenX(gxxyyy, bKernel, nx, ny);
			}
		}

		else {
			double d = 6.283185307179586D * sigma6;
			for (int i = 0; i < kLength; i++) {
				aKernel[i] = ((i * i - sigma2) * g[i] / d);
			}
			gxx = Convolvers.convolveEvenX(input, aKernel, nx, ny);
			gxx = Convolvers.convolveEvenY(gxx, g, nx, ny);
			gyy = Convolvers.convolveEvenY(input, aKernel, nx, ny);
			gyy = Convolvers.convolveEvenX(gyy, g, nx, ny);
			for (int i = 0; i < kLength; i++) {
				aKernel[i] = (i * g[i]);
				aKernel[i] /= d;
			}
			gxy = Convolvers.convolveOddX(input, aKernel, nx, ny);
			gxy = Convolvers.convolveOddY(gxy, bKernel, nx, ny);

			if (order == 4) {
				d = 6.283185307179586D * sigma10;
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = ((i * i * i * i - 6.0D * i * i * sigma2 + 3.0D * sigma4) * g[i] / d);
				}
				gxxxx = Convolvers.convolveEvenX(input, aKernel, nx, ny);
				gxxxx = Convolvers.convolveEvenY(gxxxx, g, nx, ny);
				gyyyy = Convolvers.convolveEvenY(input, aKernel, nx, ny);
				gyyyy = Convolvers.convolveEvenX(gyyyy, g, nx, ny);
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = (i * (i * i - 3.0D * sigma2) * g[i] / d);
					bKernel[i] = (i * g[i]);
				}
				gxxxy = Convolvers.convolveOddX(input, aKernel, nx, ny);
				gxxxy = Convolvers.convolveOddY(gxxxy, bKernel, nx, ny);
				gxyyy = Convolvers.convolveOddY(input, aKernel, nx, ny);
				gxyyy = Convolvers.convolveOddX(gxyyy, bKernel, nx, ny);
				for (int i = 0; i < kLength; i++) {
					aKernel[i] = ((sigma2 - i * i) * g[i]);
					aKernel[i] /= d;
				}
				gxxyy = Convolvers.convolveEvenX(input, aKernel, nx, ny);
				gxxyy = Convolvers.convolveEvenY(gxxyy, bKernel, nx, ny);
			}
		}

	}

	private double complement(double theta) {
		if (theta > 0.0D) {
			return theta - 1.5707963267948966D;
		}
		return theta + 1.5707963267948966D;
	}

	private static void normalize(double[] v) {
		double n = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		v[0] /= n;
		v[1] /= n;
	}

	private double approxZero(double n, double tol) {
		if (Math.abs(n) < tol) {
			return 0.0D;
		}
		return n;
	}

	private static void sort(double[] responses, double[] angles) {
		int N = responses.length;

		for (int i = 0; i < N - 1; i++) {
			double min = responses[i];
			int minIndex = i;
			for (int k = i + 1; k < N; k++) {
				if (responses[k] < min) {
					min = responses[k];
					minIndex = k;
				}
			}
			double temp = responses[i];
			responses[i] = min;
			responses[minIndex] = temp;
			temp = angles[i];
			angles[i] = angles[minIndex];
			angles[minIndex] = temp;
		}
	}

	private double opposite(double theta) {
		if (theta > 0.0D) {
			return theta - 3.141592653589793D;
		}
		return theta + 3.141592653589793D;
	}

	public void stop() {
		stop = true;
	}

	public boolean getStop() {
		return stop;
	}

	public static double[] convertFloatsToDoubles(float[] input) {
		if (input == null) {
			return null;
		}
		double[] output = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			output[i] = input[i];
		}
		return output;
	}

	private double pointRespM1(int i, double angle) {
		double cosT = Math.cos(angle);
		double sinT = Math.sin(angle);

		double result = a11 * (cosT * gy[i] - sinT * gx[i]);
		return result;
	}

	private double pointRespM2(int i, double angle) {
		double cosT = Math.cos(angle);
		double sinT = Math.sin(angle);

		double result = cosT * cosT * (a22 * gyy[i] + a20 * gxx[i]) + cosT * sinT * 2.0D * (a20 - a22) * gxy[i]
				+ sinT * sinT * (a22 * gxx[i] + a20 * gyy[i]);
		return result;
	}

	private double pointRespM3(int i, double angle) {
		double cosT = Math.cos(angle);
		double sinT = Math.sin(angle);
		double cosT2 = cosT * cosT;
		double sinT2 = sinT * sinT;

		double result = cosT * a11 * gy[i] - sinT * a11 * gx[i] + cosT2 * cosT * (a31 * gxxy[i] + a33 * gyyy[i])
				- sinT2 * sinT * (a31 * gxyy[i] + a33 * gxxx[i])
				- cosT2 * sinT * (3.0D * a33 * gxyy[i] - 2.0D * a31 * gxyy[i] + a31 * gxxx[i])
				+ cosT * sinT2 * (3.0D * a33 * gxxy[i] - 2.0D * a31 * gxxy[i] + a31 * gyyy[i]);
		return result;
	}

	private double pointRespM4(int i, double angle) {
		double cosT = Math.cos(angle);
		double sinT = Math.sin(angle);
		double cosTsinT = cosT * sinT;
		double cosT2 = cosT * cosT;
		double sinT2 = sinT * sinT;
		double a = (a20 - a22) * gxy[i];

		double result = cosT2 * cosT2 * (a20 * gxx[i] + a22 * gyy[i] + a40 * gxxxx[i] + a42 * gxxyy[i] + a44 * gyyyy[i])
				+ cosT2 * cosTsinT * 2.0D
						* (a + 2.0D * a40 * gxxxy[i] + a42 * (gxyyy[i] - gxxxy[i]) - 2.0D * a44 * gxyyy[i])
				+ cosT2 * sinT2
						* ((a20 + a22) * (gxx[i] + gyy[i]) + a42 * (gxxxx[i] + gyyyy[i])
								+ (6.0D * (a40 + a44) - 4.0D * a42) * gxxyy[i])
				+ sinT2 * cosTsinT * 2.0D
						* (a + 2.0D * a40 * gxyyy[i] + a42 * (gxxxy[i] - gxyyy[i]) - 2.0D * a44 * gxxxy[i])
				+ sinT2 * sinT2 * (a20 * gyy[i] + a22 * gxx[i] + a40 * gyyyy[i] + a42 * gxxyy[i] + a44 * gxxxx[i]);
		return result;
	}

	private double pointRespM5(int i, double angle) {
		double cosT = Math.cos(angle);
		double sinT = Math.sin(angle);
		double cosT2 = cosT * cosT;
		double sinT2 = sinT * sinT;
		double cosT3 = cosT2 * cosT;
		double sinT3 = sinT2 * sinT;

		double result = cosT2 * cosT3
				* (a11 * gy[i] + a31 * gxxy[i] + a33 * gyyy[i] + a51 * gxxxxy[i] + a53 * gxxyyy[i])
				+ cosT2 * cosT2 * sinT
						* (-a11 * gx[i] - a31 * gxxx[i] - (3.0D * a33 - 2.0D * a31) * gxyy[i]
								+ a51 * (4.0D * gxxxyy[i] - gxxxxx[i]) + a53 * (2.0D * gxyyyy[i] - 3.0D * gxxxyy[i]))
				+ cosT3 * sinT2
						* (2.0D * a11 * gy[i] + (3.0D * a33 - a31) * gxxy[i] + (a33 + a31) * gyyy[i]
								+ a51 * (6.0D * gxxyyy[i] - 4.0D * gxxxxy[i])
								+ a53 * (gyyyyy[i] - 6.0D * gxxyyy[i] + 3.0D * gxxxxy[i]))
				+ cosT2 * sinT3
						* (-2.0D * a11 * gx[i] - (3.0D * a33 - a31) * gxyy[i] - (a33 + a31) * gxxx[i]
								- a51 * (6.0D * gxxxyy[i] - 4.0D * gxyyyy[i])
								- a53 * (gxxxxx[i] - 6.0D * gxxxyy[i] + 3.0D * gxyyyy[i]))
				+ cosT * sinT2 * sinT2
						* (a11 * gy[i] + a31 * gyyy[i] + (3.0D * a33 - 2.0D * a31) * gxxy[i]
								- a51 * (4.0D * gxxyyy[i] - gyyyyy[i]) - a53 * (2.0D * gxxxxy[i] - 3.0D * gxxyyy[i]))
				- sinT2 * sinT3 * (a11 * gx[i] + a31 * gxyy[i] + a33 * gxxx[i] + a51 * gxyyyy[i] + a53 * gxxxyy[i]);
		return result;
	}

	private boolean approxEqual(double a, double b, double tol) {
		if (Math.abs(1.0D - a / b) < tol) {
			return true;
		}
		return false;
	}

}
