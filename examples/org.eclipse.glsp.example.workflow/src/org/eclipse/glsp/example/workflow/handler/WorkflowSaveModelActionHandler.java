/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
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
package org.eclipse.glsp.example.workflow.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.api.action.kind.SaveModelAction;
import org.eclipse.glsp.api.action.kind.SetDirtyStateAction;
import org.eclipse.glsp.api.factory.GraphGsonConfiguratorFactory;
import org.eclipse.glsp.api.model.GraphicalModelState;
import org.eclipse.glsp.api.utils.ClientOptions;
import org.eclipse.glsp.example.binalyzer.BamlNamespacePrefixMapper;
import org.eclipse.glsp.example.binalyzer.GTemplate;
import org.eclipse.glsp.example.workflow.wfgraph.TaskNode;
import org.eclipse.glsp.graph.GModelElement;
import org.eclipse.glsp.graph.GModelRoot;
import org.eclipse.glsp.graph.impl.GEdgeImpl;
import org.eclipse.glsp.server.actionhandler.BasicActionHandler;

import com.google.inject.Inject;

public class WorkflowSaveModelActionHandler extends BasicActionHandler<SaveModelAction> {
   private static final Logger LOG = Logger.getLogger(WorkflowSaveModelActionHandler.class);
   private static final String FILE_PREFIX = "file://";

   @Inject
   protected GraphGsonConfiguratorFactory gsonConfigurationFactory;

   @Override
   public List<Action> executeAction(final SaveModelAction action, final GraphicalModelState modelState) {
      saveModelState(modelState);

      return listOf(new SetDirtyStateAction(modelState.isDirty()));
   }

   private void saveModelState(final GraphicalModelState modelState) {
      Optional<String> sourceUriOpt = ClientOptions.getValue(modelState.getClientOptions(), ClientOptions.SOURCE_URI);
      if (sourceUriOpt.isPresent()) {
         String sourceUri = sourceUriOpt.get();
         String filePath = sourceUri.replace(FILE_PREFIX, "");
         GModelRoot root = modelState.getRoot();
         toXml(filePath, convertToTemplateModel(root));
      }
      //
      // convertToFile(modelState).ifPresent(file -> {
      // try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
      // Gson gson = gsonConfigurationFactory.configureGson().setPrettyPrinting().create();
      //
      // gson.toJson(root, GGraph.class, writer);
      // modelState.saveIsDone();
      // } catch (IOException e) {
      // LOG.error(e);
      // }
      // });
   }

   public void toXml(final String filePath, final GTemplate template) {
      try {
         JAXBContext contextObj = JAXBContext.newInstance(GTemplate.class);
         Marshaller marshallerObj = contextObj.createMarshaller();
         marshallerObj.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
         marshallerObj.setProperty("com.sun.xml.bind.namespacePrefixMapper", new BamlNamespacePrefixMapper());
         marshallerObj.marshal(template, new FileOutputStream(filePath));
      } catch (JAXBException je) {
         je.printStackTrace();
      } catch (IOException ie) {
         System.out.println("IOException");
      }
   }

   private GTemplate convertToTemplateModel(final GModelRoot root) {
      GTemplate templateRoot = new GTemplate();
      templateRoot.setName("sprotty");

      List<GTemplate> templates = new ArrayList<>();
      List<GModelElement> elements = root.getChildren();

      for (GModelElement element : elements) {
         if (element instanceof TaskNode) {
            TaskNode taskNode = (TaskNode) element;
            GTemplate template = new GTemplate();
            template.setId(element.getId());
            template.setName(taskNode.getName());
            template.setTaskType(taskNode.getTaskType());
            template.setType(taskNode.getType());
            template.setLayout(taskNode.getLayout());
            template.setX(taskNode.getPosition().getX());
            template.setY(taskNode.getPosition().getY());
            template.setWidth(taskNode.getSize().getWidth());
            template.setHeight(taskNode.getSize().getHeight());
            templates.add(template);
         }
      }

      for (GModelElement element2 : elements) {
         if (element2 instanceof GEdgeImpl) {
            GEdgeImpl edge = (GEdgeImpl) element2;
            GTemplate targetTemplate = templates.stream()
               .filter(template -> template.getId().equals(edge.getTargetId())).findFirst().get();
            GTemplate sourceTemplate = templates.stream()
               .filter(template -> template.getId().equals(edge.getSourceId())).findFirst().get();
            targetTemplate.getChildren().add(sourceTemplate);
            sourceTemplate.parent = targetTemplate;
         }
      }

      List<GTemplate> rootTemplates = templates.stream().filter(template -> template.parent == null)
         .collect(Collectors.toList());

      templateRoot.getChildren().addAll(rootTemplates);

      return templateRoot;
   }

   private Optional<File> convertToFile(final GraphicalModelState modelState) {
      Optional<String> sourceUriOpt = ClientOptions.getValue(modelState.getClientOptions(), ClientOptions.SOURCE_URI);
      if (sourceUriOpt.isPresent()) {
         return Optional.of(new File(sourceUriOpt.get()));
      }
      return Optional.empty();
   }
}
