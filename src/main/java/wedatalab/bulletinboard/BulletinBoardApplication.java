package wedatalab.bulletinboard;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "wedatalab.bulletinboard")
public class BulletinBoardApplication {

	public static void main(String[] args) {

		SpringApplication.run(BulletinBoardApplication.class, args);
	}

}
