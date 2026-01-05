package test.com.project.aqa;

import test.com.project.integration.AdminIntegrationTest;
import test.com.project.integration.FileSerializationTest;
import test.com.project.unit.models.PointTest;
import test.com.project.unit.models.PolynomialFunctionTest;
import test.com.project.unit.ui.AdminTest;
import test.com.project.unit.ui.UserLoginTest;
import test.com.project.unit.ui.UserTest;

@Suite
@SelectClasses({
        PolynomialFunctionTest.class,
        PointTest.class,
        AdminTest.class,
        UserTest.class,
        UserLoginTest.class,
        AdminIntegrationTest.class,
        FileSerializationTest.class
})

@SuiteDisplayName("Набор AQA тестов")
@IncludeTags({"unit", "integration"})
public class AQATestSuite {
}