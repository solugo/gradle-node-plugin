package de.solugo.gradle.node

import org.gradle.api.Plugin
import org.gradle.api.Project

class NodePlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        project.extensions.create("node", NodeExtension)

        project.node.aliases["npm"] = "npm/bin/npm-cli.js"

        project.ext.NodeTask = NodeTask

        final cleanTask = project.tasks.findByPath("clean")
        if (cleanTask != null) {
            cleanTask.doLast {
                delete project.file("node_modules")
            }
        }

        project.tasks.create("npmInstall", NodeTask).doFirst {
            executable = "npm"
            args = ["install"]

            final def args = project.getProperties().get(NodeTask.PROPERTY_ARGS)
            if (project.file("node_modules").exists() && args == null) {
                return "SKIPPED"
            }
        }

        project.tasks.addRule("Pattern: npmRun<script>") { String taskName ->
            if (project.tasks.findByPath(taskName) == null && taskName.startsWith("npmRun")) {
                final String target = (taskName - "npmRun")
                project.tasks.create(taskName, NodeTask).doFirst {
                    executable = "npm"
                    if (target.length() > 0) {
                        args = ["run", target.substring(0, 1).toLowerCase() + target.substring(1)]
                    } else {
                        args = ["run"]
                    }
                }
            }
        }

        project.tasks.addRule("Pattern: npm<task>") { String taskName ->
            if (project.tasks.findByPath(taskName) == null && taskName.startsWith("npm")) {
                final String target = (taskName - "npm")
                project.tasks.create(taskName, NodeTask).doFirst {
                    executable = "npm"
                    if (target.length() > 0) {
                        args = [target.substring(0, 1).toLowerCase() + target.substring(1)]
                    }
                }
            }
        }

        project.tasks.addRule("Pattern: node<task>") { String taskName ->
            if (project.tasks.findByPath(taskName) == null && taskName.startsWith("node")) {
                final String target = (taskName - "node")
                project.tasks.create(taskName, NodeTask).doFirst {
                    if (target.length() > 0) {
                        executable = target.substring(0, 1).toLowerCase() + target.substring(1)
                    }
                }
            }
        }
    }

}

