package igrek.forceawaken.dagger;

import igrek.forceawaken.MainApplication;


public class DaggerIOC {
	
	private static FactoryComponent appComponent;
	
	private DaggerIOC() {
	}
	
	public static void init(MainApplication application) {
		appComponent = DaggerFactoryComponent.builder()
				.factoryModule(new FactoryModule(application))
				.build();
	}
	
	public static FactoryComponent getFactoryComponent() {
		return appComponent;
	}
	
	/**
	 * only for testing purposes
	 * @param component
	 */
	public static void setFactoryComponent(FactoryComponent component) {
		appComponent = component;
	}
	
}
