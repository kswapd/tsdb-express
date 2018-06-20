import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */



@Measurement(name = "cpu")
public class Cpu {
	@Column(name = "time")
	private String time;

	@Column(name = "host")
	private String host;
	@Column(name = "idle")
	private Integer idle;

	@Column(name = "user")
	private Integer user;

	@Column(name = "system")
	private Integer system;

	public String toString(){
		return String.format("cpu info[host = %s]:[time:%s, user:%d, system:%d, idle:%d]", host,time, user,system, idle);
	}


	// getters (and setters if you need)
}
