import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import models.Post;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
public class ApiTest {
 String token = "Ji7oZ9NUYE0PxzBU-a5kxwSasA0A8FSSgjbU";
  @BeforeClass
  public void beforeClass() {
      RestAssured.baseURI = "https://gorest.co.in/";

  }
    @Test
    public void test1_1() {
        System.out.println("Получили список всех пользователей: ");
        given().auth().oauth2(token)
                .when().get("public-api/users")
                .then().log().all()
                .statusCode(200);

    }

    @DataProvider(name = "username")
    public Object[][] username()
    {
        return  new Object[][]{
                {"Catalina"}
        };
    }
    @Test(dataProvider = "username")
    public void test1_2(String first_name)
    {
        System.out.println("Получен список пользователей с указанным именем: ");
        given().auth().oauth2(token)
                .when().get("public-api/users?first_name=" + first_name)
                .then().log().all()
                .assertThat()
                .body("result.first_name", equalTo(first_name))
                .statusCode(200);

    }


    @DataProvider(name = "new_user")
    public Object[][] new_user()
    {
        return new Object[][] {{"Stas", "Prohorov", "1999-02-07","male", "1234@gmail.com", "+8 (902) 111-78-44", "active"}};
    }

    @Test(dataProvider = "new_user")
    public void test1_3(String first_name, String last_name,  String dob, String gender, String email, String phone, String Status)
    {
        Post post1 = new Post(first_name, last_name,  gender,dob, email, phone, Status);
        System.out.println("создали нового пользователя");
        given().auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(post1)
                .when().post("public-api/users")
                .then().log().all()
                .body("result.first_name", equalTo(first_name))
                .body("result.last_name", equalTo(last_name))
                .body("result.gender", equalTo(gender))
                .body("result.email", equalTo(email))
                .body("result.dob", equalTo(dob))
                .body("result.phone", equalTo(phone))
                .body("result.status", equalTo(Status))
                .statusCode(302);

    }

    @DataProvider(name = "ID_1")
    public Object[][] ID_1()
    {
        return  new Object[][]{
                {1749}
        };
    }
    @Test(dataProvider = "ID_1")
    public void test1_4(int ID)
    {
        System.out.println("Получен пользователь с выбранным ID:");
        given().auth().oauth2(token)
                .when().get("public-api/users/"+ ID)
                .then()
                .log().all()
                .body("result.ID", equalTo(Integer.toString(ID)))
                .statusCode(200);

    }

    @DataProvider(name = "ID_2")
    public Object[][] ID_2()
    {
        return new Object[][] {{1750, "Halie", "Tillman",  "male", "1999-02-07", "123@gmail.com", "+8 (902) 111-77-44" , "active"}};
    }
    @Test(dataProvider = "ID_2")
    public void test1_5(int ID,String first_name, String last_name, String gender, String dob, String email, String phone, String Status )
    {
        Post post =  new Post( first_name, last_name,gender, dob, email, phone, Status);
        System.out.println("Изменить пользователя с указанным ID:");
        given().auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(post)
                .when().put("public-api/users/" + ID)
                .then()
                .log().all().body("result.id", equalTo(Integer.toString(ID)))
                .body("result.first_name", equalTo(first_name))
                .body("result.last_name", equalTo(last_name))
                .body("result.gender", equalTo(gender))
                .body("result.email", equalTo(email))
                .body("result.dob", equalTo(dob))
                .body("result.phone", equalTo(phone))
                .body("result.status", equalTo(Status))
                .statusCode(200);
    }

    @DataProvider(name = "ID_3")
    public Object[][] ID_3()
    {
        return new Object[][] {{1752}};
    };
    @Test(dataProvider = "ID_3")
    public void test1_6(int ID) {
        System.out.println("Удалить пользователя с указанным ID:");
        given().auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when().delete("public-api/users/" + ID)
                .then().log().all()
                .body("result", equalTo(null))

                .statusCode(200);
    }

    @DataProvider(name = "token_value")
    public Object[][] token_value()
    {
        return new Object[][] {{""}, {"qqwwee"}};
    };
    @Test(dataProvider = "token_value")
    public void test2_1(String tok) {
        System.out.println("Без указания токена авторизации и с некорректным токеном:");
        given().auth().oauth2(tok)
                .when().get("public-api/users")
                .then().log().all()
                .body("result.status", equalTo(401))
                .body("result.message", equalTo("Your request was made with invalid credentials."))

               .statusCode(200);

    }

    @DataProvider(name = "invalid_body")
    public Object[][] invalid_body()
    {
        return new Object[][] {{"Stas", "Prohorov", "1999-02-07","male", "1234@gmail.com", "+8 (902) 111-78-44", "active"}};
    }

    @Test(dataProvider = "invalid_body")
    public void test2_2(String first_name, String last_name,  String dob, String gender, String email, String phone, String Status)
    {
        Post post1 = new Post(first_name, last_name,  gender,dob, email, phone, Status);
        System.out.println("C некорректным форматом тела запроса:");
        given().auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(post1)
                .when().post("public-api/users")
                .then().log().all()
                .body("_meta.code", equalTo(422))
                .body("result.message", equalTo("Email 1234@gmail.com has already been taken."))
        .statusCode(200);
    }

    @DataProvider(name = "Invalid_delete")
    public Object [][] Invalid_delete()
    {
        return new Object[][] {{228}};
    }
    @Test(dataProvider = "Invalid_delete")
    public void test2_3(int ID)
    {

        System.out.println("Delete:");
        given().auth().oauth2(token)
                .contentType(ContentType.JSON)
                .when().delete("public-api/users/" + ID)
                .then().log().all()
                .body("_meta.code", equalTo(404))

                .statusCode(200);
    }

}


