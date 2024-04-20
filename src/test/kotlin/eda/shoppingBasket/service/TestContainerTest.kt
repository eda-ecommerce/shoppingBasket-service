package eda.shoppingBasket.service

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestContainerTest
{
    companion object{
        val db = MySQLContainer("mysql")

        @JvmStatic
        @BeforeAll
        fun startDBContainer(){
            db.start()
        }

        @JvmStatic
        @AfterAll
        fun stopDBContainer(){
            db.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDbContainer(registry: DynamicPropertyRegistry){
            registry.add("spring.datasource.url", db::getJdbcUrl)
            registry.add("spring.datasource.username", db::getUsername)
            registry.add("spring.datasource.password", db::getPassword)
        }
    }
    @Test
    fun testDbRunning(){
        assert(db.isRunning)
    }

    @Test
    fun testRepo(){

    }
}