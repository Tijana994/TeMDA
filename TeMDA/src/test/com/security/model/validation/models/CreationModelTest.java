package test.com.security.model.validation.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.security.model.validation.annotations.enums.Constants;
import com.security.model.validation.annotations.enums.CreatedObjectLocation;
import com.security.model.validation.annotations.enums.ParametersObjectsLocation;
import com.security.model.validation.models.CreationModel;

import test.com.security.model.validation.models.Fixtures.Holder;
import test.com.security.model.validation.models.Fixtures.User;

public class CreationModelTest {

	@Test
	public void constructor_StoresValuesAndReturnedObjectClass() {
		var returned = new User("Ana");
		var joinPoint = Fixtures.joinPoint(new String[0]);
		var model = new CreationModel(returned, joinPoint, CreatedObjectLocation.Property, ParametersObjectsLocation.Parameter);

		assertSame(returned, model.getReturnedObject());
		assertEquals(User.class, model.getReturnedObjectClass());
		assertSame(joinPoint, model.getJoinPoint());
		assertEquals(CreatedObjectLocation.Property, model.getCreatedLocation());
		assertEquals(ParametersObjectsLocation.Parameter, model.getParametersLocation());
		assertNull(model.getObject());
		assertNull(model.getParameterObject());
	}

	@Test
	public void constructor_NoReturnedObject_NoReturnedObjectClass() {
		var model = new CreationModel(null, null, CreatedObjectLocation.Return, ParametersObjectsLocation.Property);
		assertNull(model.getReturnedObjectClass());
	}

	@Test
	public void setObject_StoresObjectAndClass() {
		var model = Fixtures.propertyModel(new Holder("x"));
		assertEquals(Holder.class, model.getObjectClass());
		assertEquals("x", ((Holder) model.getObject()).value);
	}

	@Test
	public void constructor_PropertyInParameterObject_ReadsParameterByNameIgnoringCase() {
		var user = new User("Ana");
		var joinPoint = Fixtures.joinPoint(new String[] { "id", "user" }, "1", user);
		var model = new CreationModel(null, new Holder(null), joinPoint, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInParameterObject, "USER");

		assertSame(user, model.getParameterObject());
		assertEquals(User.class, model.getParameterObjectClass());
	}

	@Test
	public void constructor_PropertyInParameterObject_UnknownParameter_NoParameterObject() {
		var joinPoint = Fixtures.joinPoint(new String[] { "id" }, "1");
		var model = new CreationModel(null, new Holder(null), joinPoint, CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInParameterObject, "user");

		assertNull(model.getParameterObject());
		assertNull(model.getParameterObjectClass());
	}

	@Test
	public void constructor_PropertyInParameterObject_EmptyName_NoParameterObject() {
		var model = new CreationModel(null, new Holder(null), Fixtures.joinPoint(new String[0]), CreatedObjectLocation.Return,
				ParametersObjectsLocation.PropertyInParameterObject, Constants.Empty);

		assertNull(model.getParameterObject());
	}

	@Test
	public void constructor_OtherLocation_ParameterObjectIsNotRead() {
		var model = new CreationModel(null, new Holder(null), null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.Property, "user");

		assertNull(model.getParameterObject());
	}

	@Test
	public void constructorWithObject_StoresObjectAndClass() {
		var holder = new Holder(null);
		var model = new CreationModel(null, holder, null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.Property, Constants.Empty);

		assertSame(holder, model.getObject());
		assertEquals(Holder.class, model.getObjectClass());
	}

	@Test
	public void constructorWithObject_NullObject_NoClass() {
		var model = new CreationModel(null, null, null, CreatedObjectLocation.Return,
				ParametersObjectsLocation.Property, Constants.Empty);

		assertNull(model.getObjectClass());
	}
}
