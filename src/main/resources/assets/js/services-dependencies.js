require(["renderer"],
    function (renderer) {

        // transform a ServicesDependencies into a Digraph
        function servicesDependenciesToDigraph(data) {

            var applicationsByName = {};

            var nodeNameFromInfo = function (applicationName, serviceName) {
                var application = applicationsByName[applicationName];
                if (serviceName) {
                    return "node_service_" + application.swaggerServicesByName[serviceName].id;
                } else {
                    return "node_app_" + application.id;
                }
            }

            var swaggerServicesCurrentId = 0;

            var result = 'digraph ServiceDependencies {\n';

            // create the application nodes
            data.applications.forEach(function (application) {
                applicationsByName[application.name] = application;

                // any declared service ?
                if (application.swaggerServices.length == 0) {
                    // no : it's a single node
                    result += '\tnode_app_' + application.id + ' [label="' + application.name + '", shape=diamond];\n\n';
                } else {
                    // yes : then create a sugraph
                    application.swaggerServicesByName = {};

                    result += '\tsubgraph cluster_app_' + application.id + ' {\n'
                    '\n';
                    application.swaggerServices.forEach(function (swaggerService) {
                        var currentId = swaggerServicesCurrentId;
                        swaggerServicesCurrentId++;
                        swaggerService.id = currentId;
                        application.swaggerServicesByName[swaggerService.name] = swaggerService;
                        result += '\t\tnode_service_' + currentId + ' [label="' + swaggerService.name + '"];\n'
                    });
                    result += '\t\tlabel="' + application.name + '";'
                    result += '\t};\n\n'
                }
            });

            // create the links
            data.serviceCalls.forEach(function (serviceCall) {
                var nodeNameFrom = nodeNameFromInfo(serviceCall.applicationFrom, serviceCall.serviceFrom);
                var nodeNameTo = nodeNameFromInfo(serviceCall.applicationTo, serviceCall.serviceTo);
                result += '\t ' + nodeNameFrom + ' -> ' + nodeNameTo + ' [label="' + serviceCall.count + '"];\n'
            });

            result += "}";
            return result;
        }

        $.get('/services/dependencies', function (data) {

            // initialize svg stage
            renderer.init("#graph");

            // update stage with new dot source
            var dotData = servicesDependenciesToDigraph(data);
            renderer.render(dotData);
        });
    });