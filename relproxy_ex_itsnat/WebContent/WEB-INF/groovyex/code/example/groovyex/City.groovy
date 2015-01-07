package example.groovyex;

/**
 *
 * @author jmarranz
 */
class City 
{
    def name;

    City(String name)
    {
        this.name = name;
    }

    def getName() { return name; }
}
