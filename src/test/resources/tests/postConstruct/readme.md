simple test shows basic usage of hardwire:

you have to create a TestModule which acts as "context" for the current package and its subpackages.

A TestModuleBase class will be generated which has to be extended by your module to be instantiable.