/*******************************************************************************
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
 ******************************************************************************/
package org.eclipse.glsp.api.action.kind;

import org.eclipse.glsp.api.action.Action;
import org.eclipse.glsp.graph.GModelRoot;

public class UpdateModelAction extends Action {

   private GModelRoot newRoot;
   private boolean animate = true;

   public UpdateModelAction() {
      super(Action.Kind.UPDATE_MODEL);
   }

   public UpdateModelAction(final GModelRoot newRoot, final boolean animate) {
      this();
      this.newRoot = newRoot;
      this.animate = animate;
   }

   public GModelRoot getNewRoot() { return newRoot; }

   public boolean isAnimate() { return animate; }

   public void setAnimate(final boolean animate) { this.animate = animate; }
}
