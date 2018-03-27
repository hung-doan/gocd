/*
 * Copyright 2018 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.thoughtworks.go.spark.spa;

import com.thoughtworks.go.server.service.SecurityService;
import com.thoughtworks.go.server.service.support.toggle.Toggles;
import com.thoughtworks.go.spark.Routes;
import com.thoughtworks.go.spark.SparkController;
import com.thoughtworks.go.spark.spring.SPAAuthenticationHelper;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.TemplateEngine;

import java.util.HashMap;

import static spark.Spark.*;

public class NewDashboardDelegate implements SparkController {
    private final SPAAuthenticationHelper authenticationHelper;
    private final TemplateEngine engine;
    private final SecurityService securityService;

    public NewDashboardDelegate(SPAAuthenticationHelper authenticationHelper, TemplateEngine engine, SecurityService securityService) {
        this.authenticationHelper = authenticationHelper;
        this.engine = engine;
        this.securityService = securityService;
    }


    @Override
    public String controllerBasePath() {
        return Routes.NewDashboardSPA.BASE;
    }

    @Override
    public void setupRoutes() {
        path(controllerBasePath(), () -> {
            before("", authenticationHelper::checkUserAnd401);
            get("", this::index, engine);
        });
    }

    public ModelAndView index(Request request, Response response) {
        HashMap<Object, Object> object = new HashMap<Object, Object>() {{
            put("viewTitle", "Dashboard");
            put("isQuickEditPageEnabled", Toggles.isToggleOn(Toggles.PIPELINE_CONFIG_SINGLE_PAGE_APP) && Toggles.isToggleOn(Toggles.QUICK_EDIT_PAGE_DEFAULT));
            put("isNewDashboardPageEnabled", Toggles.isToggleOn(Toggles.NEW_DASHBOARD_PAGE_DEFAULT));
            put("shouldShowAnalyticsIcon", securityService.isUserAdmin(currentUsername()));
        }};
        return new ModelAndView(object, "new_dashboard/index.vm");
    }
}