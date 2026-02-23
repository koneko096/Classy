package id.koneko096.classy.util;

public class Math {

  private Math() {}

  public static double sqrt(double v) {
    final long x = Double.doubleToLongBits(v) >> 32;
    double y = Double.longBitsToDouble((x + 1072632448) << 31);

    for (int i = 0; i < 16; i++) {
      y = (y + v / y) * 0.5;
    }

    return y;
  }

  public static double sqr(double x) {
    return x * x;
  }
}
