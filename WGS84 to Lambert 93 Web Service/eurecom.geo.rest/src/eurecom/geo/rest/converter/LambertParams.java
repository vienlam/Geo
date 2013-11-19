package eurecom.geo.rest.converter;

import java.util.HashMap;
import java.util.Map;

public class LambertParams {
	
	@SuppressWarnings("serial")
	public static final Map<String, Double> Lambert93 = new HashMap<String, Double>() {{
		put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
		put("lamda0Paris", 0.0);
		put("n", 0.725_607_7650);
		put("c", 11_754_255.426);
		put("e", 0.081_819_191_042_815_79);
		put("xs", 700_000.0);
		put("ys", 12_655_612.050);
	}};
	
	@SuppressWarnings("serial")
	public static final Map<String, Double> LambertI = new HashMap<String, Double>() {{
		put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
		put("lamda0Paris", 0.0);
		put("n", 0.760_405_965_6);
		put("c", 11_603_796.98);
		put("e", 0.081_819_191_042_815_79);
		put("xs", 600_000.0);
		put("ys", 5_657_616.674);
	}};
	
	@SuppressWarnings("serial")
	public static final Map<String, Double> LambertII = new HashMap<String, Double>() {{
		put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
		put("lamda0Paris", 0.0);
		put("n", 0.728_968_627_4);
		put("c", 11_745_793.39);
		put("e", 0.081_819_191_042_815_79);
		put("xs", 600_000.0);
		put("ys", 6_199_695.768);
	}};
	
	@SuppressWarnings("serial")
	public static final Map<String, Double> LambertIII = new HashMap<String, Double>() {{
		put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
		put("lamda0Paris", 0.0);
		put("n", 0.695_912_766_6);
		put("c", 11_947_992.52);
		put("e", 0.081_819_191_042_815_79);
		put("xs", 600_000.0);
		put("ys", 6_791_905.085);
	}};
	
	@SuppressWarnings("serial")
	public static final Map<String, Double> LambertIV = new HashMap<String, Double>() {{
		put("lamda0Green", 0.052_359_877_559_83); // 3 degrees
		put("lamda0Paris", 0.0);
		put("n", 0.671_267_932_2);
		put("c", 12_136_281.99);
		put("e", 0.081_819_191_042_815_79);
		put("xs", 234.358);
		put("ys", 7_239_161.542);
	}};
	
}
