package example.javaex;

import java.util.LinkedList;

/**
 *
 * @author jmarranz
 */
public class FalseDB 
{
    protected LinkedList<City> cities;

    public FalseDB()
    {
        cities = new LinkedList<City>();
        cities.add(new City("Madrid"));
        cities.add(new City("Barcelona"));
        cities.add(new City("Bilbao"));
    }

    public LinkedList<City> getCityList() { return cities;  /*cities;*/ }
}