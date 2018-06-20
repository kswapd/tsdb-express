import com.dcits.tsdb.annotations.Column;
import com.dcits.tsdb.annotations.Measurement;
import com.dcits.tsdb.annotations.Tag;

/**
 * Created by kongxiangwen on 6/19/18 w:25.
 */



@Measurement(name = "cpu")
public class Cpu {
	@Column(name = "time")
	private String time;

	@Tag(name = "host")
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getIdle() {
		return idle;
	}

	public void setIdle(Integer idle) {
		this.idle = idle;
	}

	public Integer getUser() {
		return user;
	}

	public void setUser(Integer user) {
		this.user = user;
	}

	public Integer getSystem() {
		return system;
	}

	public void setSystem(Integer system) {
		this.system = system;
	}


	// getters (and setters if you need)
}
