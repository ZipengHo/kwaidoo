package cmn.http.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于将Web请求映射到请求处理类中的方法的注解，支持灵活的方法签名。
 *
 * <p>
 * Spring MVC和Spring WebFlux都通过其各自的模块和包结构中的
 * {@code RequestMappingHandlerMapping}和{@code RequestMappingHandlerAdapter}
 * 支持这个注解。关于每个框架支持的处理器方法参数和返回类型的完整列表，请参考以下链接：
 * <ul>
 * <li>Spring MVC <a href=
 * "https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-arguments">方法参数</a>
 * 和 <a href=
 * "https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#mvc-ann-return-types">返回值</a>
 * </li>
 * <li>Spring WebFlux <a href=
 * "https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-arguments">方法参数</a>
 * 和 <a href=
 * "https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#webflux-ann-return-types">返回值</a>
 * </li>
 * </ul>
 *
 * <p>
 * <strong>注意：</strong>这个注解可以在类级别和方法级别使用。在大多数情况下，应用程序更倾向于在方法级别使用HTTP方法特定的变体
 * {@link GetMapping @GetMapping}、{@link PostMapping @PostMapping}、
 * {@link PutMapping @PutMapping}、{@link DeleteMapping @DeleteMapping}或
 * {@link PatchMapping @PatchMapping}。
 *
 * <p>
 * <strong>注意：</strong>这个注解不能与其他在同一元素（类、接口或方法）上声明的
 * {@code @RequestMapping}注解一起使用。如果在同一元素上检测到多个
 * {@code @RequestMapping}注解，将会记录警告，并且只会使用第一个映射。这也适用于组合的
 * {@code @RequestMapping}注解，如{@code @GetMapping}、{@code @PostMapping}等。
 *
 * <p>
 * <strong>注意：</strong>当使用控制器接口时（例如，用于AOP代理），请确保将所有映射注解（如
 * {@code @RequestMapping}和{@code @SessionAttributes}）一致地放在控制器接口上，而不是实现类上。
 *
 * @author Juergen Hoeller
 * @author Arjen Poutsma
 * @author Sam Brannen
 * @since 2.5
 * @see GetMapping
 * @see PostMapping
 * @see PutMapping
 * @see DeleteMapping
 * @see PatchMapping
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

	/**
	 * 为这个映射指定一个名称。
	 * <p>
	 * <b>支持在类级别和方法级别使用！</b> 如果同时在两个级别使用，则通过“#”作为分隔符拼接组合名称。
	 * 
	 * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder
	 * @see org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy
	 */
	String name() default "";

	/**
	 * 路径映射的URI——例如，{@code "/profile"}。
	 * <p>
	 * 支持Ant风格的路径模式（例如，{@code "/profile/**"}）。
	 * 在方法级别，支持相对于类级别主要映射的相对路径（例如，{@code "edit"}）。
	 * 路径映射的URI可以包含占位符（例如，<code>"/${profile_path}"</code>）。
	 * <p>
	 * <b>支持在类级别和方法级别使用！</b> 在类级别使用时，所有方法级别的映射都会继承这个主要映射，并针对特定的处理器方法进行细化。
	 * <p>
	 * <strong>注意：</strong>如果处理器方法没有显式映射到任何路径，则该方法实际上映射到空路径。
	 * 
	 * @since 4.2
	 */
	String[] path() default {};

	/**
	 * 映射的HTTP请求方法，用于细化主要映射： GET、POST、HEAD、OPTIONS、PUT、PATCH、DELETE、TRACE。
	 * <p>
	 * <b>支持在类级别和方法级别使用！</b> 在类级别使用时，所有方法级别的映射都会继承这个HTTP方法限制。
	 */
	RequestMethod[] method() default {};
//
//	/**
//	 * 映射请求的参数，用于细化主要映射。
//	 * <p>
//	 * 任何环境下的格式相同：一系列“myParam=myValue”风格的表达式，
//	 * 只有当每个参数具有给定值时，请求才会被映射。表达式可以通过“!=”操作符进行否定，
//	 * 例如“myParam!=myValue”。也支持“myParam”风格的表达式，
//	 * 这样的参数必须存在于请求中（可以具有任何值）。最后，“!myParam”风格的表达式 表示指定的参数不应存在于请求中。
//	 * <p>
//	 * <b>支持在类级别和方法级别使用！</b> 在类级别使用时，所有方法级别的映射都会继承这个参数限制。
//	 */
//	String[] params() default {};
//
//	/**
//	 * 映射请求的头信息，用于细化主要映射。
//	 * <p>
//	 * 任何环境下的格式相同：一系列“My-Header=myValue”风格的表达式，
//	 * 只有当每个头信息具有给定值时，请求才会被映射。表达式可以通过“!=”操作符进行否定，
//	 * 例如“My-Header!=myValue”。也支持“My-Header”风格的表达式，
//	 * 这样的头信息必须存在于请求中（可以具有任何值）。最后，“!My-Header”风格的表达式 表示指定的头信息不应存在于请求中。
//	 * <p>
//	 * 还支持媒体类型通配符（*），例如“Accept”和“Content-Type”头信息。 例如：
//	 * 
//	 * <pre class="code">
//	 * &#064;RequestMapping(value = "/something", headers = "content-type=text/*")
//	 * </pre>
//	 * 
//	 * 将匹配请求的“Content-Type”为“text/html”、“text/plain”等。
//	 * <p>
//	 * <b>支持在类级别和方法级别使用！</b> 在类级别使用时，所有方法级别的映射都会继承这个头信息限制。
//	 * 
//	 * @see cmn.http.MediaType
//	 */
//	String[] headers() default {};
//
	/**
	 * 通过可消费的媒体类型细化主要映射。包含一个或多个媒体类型， 其中之一必须与请求的{@code Content-Type}头信息匹配。例如：
	 * 
	 * <pre class="code">
	 * consumes = "text/plain"
	 * consumes = {"text/plain", "application/*"}
	 * consumes = MediaType.TEXT_PLAIN_VALUE
	 * </pre>
	 * <p>
	 * 如果声明的媒体类型包含参数，并且请求的{@code "content-type"}
	 * 也包含该参数，则参数值必须匹配。否则，如果请求的媒体类型不包含该参数，则忽略该参数。
	 * <p>
	 * 表达式可以通过“!”操作符进行否定，例如“!text/plain”，
	 * 匹配所有请求的{@code Content-Type}不是“text/plain”的请求。
	 * <p>
	 * <b>支持在类级别和方法级别使用！</b> 如果在两个级别都指定，则方法级别的条件会覆盖类级别的条件。
	 * 
	 * @see cmn.http.MediaType
	 */
	String[] consumes() default {};

	/**
	 * 通过可生产的媒体类型细化主要映射。包含一个或多个媒体类型，其中必须通过内容协商选择一个媒体类型，
	 * 以匹配请求的“可接受”媒体类型。通常这些是从请求的 {@code "Accept"} 头信息中提取的，
	 * 但也可能来自查询参数或其他来源。例如：
	 * <pre class="code">
	 * produces = "text/plain"
	 * produces = {"text/plain", "application/*"}
	 * produces = MediaType.TEXT_PLAIN_VALUE
	 * produces = "text/plain;charset=UTF-8"
	 * </pre>
	 * <p>如果声明的媒体类型包含参数（例如，“charset=UTF-8”、“type=feed”、“type=entry”），
	 * 并且请求中兼容的媒体类型也包含该参数，则参数值必须匹配。否则，如果请求的媒体类型不包含该参数，
	 * 则假定客户端接受任何值。
	 * <p>表达式可以通过“!”操作符进行否定，例如“!text/plain”，
	 * 匹配所有请求的 {@code Accept} 不是“text/plain”的请求。
	 * <p><b>支持在类级别和方法级别使用！</b>
	 * 如果在两个级别都指定，则方法级别的`produces`条件会覆盖类级别的条件。
	 * @see cmn.http.MediaType
	 */
	String[] produces() default {};

}
