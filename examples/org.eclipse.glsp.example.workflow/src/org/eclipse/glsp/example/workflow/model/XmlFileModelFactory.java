/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.glsp.example.workflow.model;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.glsp.api.action.kind.RequestModelAction;
import org.eclipse.glsp.api.factory.GraphGsonConfiguratorFactory;
import org.eclipse.glsp.api.factory.ModelFactory;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.utils.ClientOptions;
import org.eclipse.glsp.example.binalyzer.GTemplate;
import org.eclipse.glsp.example.workflow.utils.WorkflowBuilder.TaskNodeBuilder;
import org.eclipse.glsp.example.workflow.wfgraph.TaskNode;
import org.eclipse.glsp.example.workflow.wfgraph.WfgraphFactory;
import org.eclipse.glsp.graph.GDimension;
import org.eclipse.glsp.graph.GEdge;
import org.eclipse.glsp.graph.GGraph;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.GPoint;
import org.eclipse.glsp.graph.impl.GDimensionImpl;
import org.eclipse.glsp.graph.impl.GEdgeImpl;
import org.eclipse.glsp.graph.impl.GGraphImpl;
import org.eclipse.glsp.graph.impl.GPointImpl;

import com.google.inject.Inject;

/**
 * A base class which can be used for all model factories that load an SModel
 * from a json file.
 *
 */
public class XmlFileModelFactory implements ModelFactory {
   private static final String FILE_PREFIX = "file://";

   @Inject
   private GraphGsonConfiguratorFactory gsonConfigurationFactory;
   private GModelRoot modelRoot;

   WfgraphFactory factory = WfgraphFactory.eINSTANCE;

   @Override
   public GModelRoot loadModel(final RequestModelAction action, final GraphicalModelState modelState) {
      String sourceURI = action.getOptions().get(ClientOptions.SOURCE_URI);
      String filePath = sourceURI.replace(FILE_PREFIX, "");
      GTemplate root = fromXml(filePath);
      modelRoot = convertToGlsp(root);
      return modelRoot;
   }

   protected File convertToFile(final String sourceURI) {
      if (sourceURI != null) {
         return new File(sourceURI.replace(FILE_PREFIX, ""));
      }
      return null;
   }

   public static GTemplate fromXml(final String filePath) {
      try {
         File file = new File(filePath);
         JAXBContext jaxbContext = JAXBContext.newInstance(GTemplate.class);

         Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
         GTemplate root = (GTemplate) jaxbUnmarshaller.unmarshal(file);

         return root;
      } catch (JAXBException e) {
         e.printStackTrace();
         return null;
      }
   }

   public GModelRoot convertToGlsp(final GTemplate root) {
      // TODO: Convert GTemplate to TaskNode
      // Children and parent relationship converts to GEdgeImpl

      GGraph glspRoot = new GGraphImpl();
      glspRoot.setId("sprotty");
      glspRoot.setType("graph");

      traverseDepthFirst(root, glspRoot);

      return glspRoot;
   }

   private void traverseDepthFirst(final GTemplate template, final GModelRoot parent) {
      if (template.getChildren().size() == 0) {
         return;
      }

      for (GTemplate child : template.getChildren()) {
         traverseDepthFirst(child, parent);

         // createEdgeNode
         if (template.getId() != null) {
            GEdge edge = new GEdgeImpl();
            edge.setSourceId(child.getId());
            edge.setTargetId(template.getId());
            edge.setParent(parent);
            edge.setId(child.getId() + "_" + template.getId());
            edge.setType("edge");
         }

         // createTaskNode
         String taskId = child.getId();
         String taskName = child.getName();
         String taskType = child.getTaskType();
         GPoint position = new GPointImpl();
         position.setX(child.getX());
         position.setY(child.getY());
         TaskNode taskNode = new TaskNodeBuilder(taskId, taskName, taskType, 0) //
            .position(position) //
            .build();

         taskNode.setId(child.getId());
         taskNode.setName(child.getName());
         taskNode.setTaskType(child.getTaskType());
         taskNode.setType(child.getType());
         taskNode.setLayout(child.getLayout());
         GPoint position2 = new GPointImpl();
         position.setX(child.getX());
         position.setY(child.getY());
         taskNode.setPosition(position);
         GDimension size = new GDimensionImpl();
         size.setHeight(child.getHeight());
         size.setWidth(child.getWidth());
         taskNode.setSize(size);
         taskNode.setParent(parent);
      }
   }
}
