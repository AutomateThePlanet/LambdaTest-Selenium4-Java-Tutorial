/*
 * Copyright 2021 Automate The Planet Ltd.
 * Author: Anton Angelov
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package layout;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class LayoutComponentValidationsBuilder implements LayoutComponent {
    public LayoutPreciseValidationBuilder above(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateAboveOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.ABOVE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.ABOVE));
    }

    public LayoutPreciseValidationBuilder right(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateRightOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.RIGHT),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.RIGHT));
    }

    public LayoutPreciseValidationBuilder left(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateLeftOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.LEFT),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.LEFT));
    }

    public LayoutPreciseValidationBuilder below(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateBelowOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.BELOW),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.BELOW));
    }

    public LayoutPreciseValidationBuilder topInside(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateTopInsideOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.TOP_INSIDE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.TOP_INSIDE));
    }

    public LayoutPreciseValidationBuilder inside(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateTopInsideOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.INSIDE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.INSIDE));
    }

    public LayoutPreciseValidationBuilder bottomInside(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateBottomInsideOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.BOTTOM_INSIDE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.BOTTOM_INSIDE));
    }

    public LayoutPreciseValidationBuilder leftInside(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateLeftInsideOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.LEFT_INSIDE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.LEFT_INSIDE));
    }

    public LayoutPreciseValidationBuilder rightInside(LayoutComponent secondLayoutComponent) {
        return new LayoutPreciseValidationBuilder(
                () -> calculateRightInsideOfDistance(this, secondLayoutComponent),
                () -> buildNotificationValidationMessage(secondLayoutComponent, LayoutOptions.RIGHT_INSIDE),
                () -> buildFailedValidationMessage(secondLayoutComponent, LayoutOptions.RIGHT_INSIDE));
    }

    public FinishValidationBuilder alignedVerticallyAll(LayoutComponent... layoutComponents) {
        Integer baseLineRightY = this.getLocation().getX() + this.getSize().getWidth() / 2;
        Integer baseLineLeftY = this.getLocation().getX();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var rightX = c.getLocation().getX() + c.getSize().getWidth() / 2;
            var leftX = c.getLocation().getX();
            combinedPredicate.and((r) -> baseLineRightY.equals(rightX)).and((r) -> baseLineLeftY.equals(leftX));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_RIGHT) +
                        buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineLeftY, LayoutOptions.ALIGNED_VERTICALLY_LEFT),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_RIGHT) +
                        buildFailedAlignValidationMessage(comparingComponentsNames, baseLineLeftY, LayoutOptions.ALIGNED_VERTICALLY_LEFT));
    }

    public FinishValidationBuilder alignedVerticallyCentered(LayoutComponent... layoutComponents) {
        Integer baseLineRightY = this.getLocation().getX() + this.getSize().getWidth() / 2;
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var rightX = c.getLocation().getX() + c.getSize().getWidth() / 2;
            combinedPredicate.and((r) -> baseLineRightY.equals(rightX));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_CENTERED),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_CENTERED));
    }

    public FinishValidationBuilder alignedVerticallyRight(LayoutComponent... layoutComponents) {
        Integer baseLineRightY = this.getLocation().getX() + this.getSize().getWidth();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var rightX = c.getLocation().getX() + c.getSize().getWidth();
            combinedPredicate.and((r) -> baseLineRightY.equals(rightX));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_RIGHT),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineRightY, LayoutOptions.ALIGNED_VERTICALLY_RIGHT));
    }

    public FinishValidationBuilder alignedVerticallyLeft(LayoutComponent... layoutComponents) {
        Integer baseLineLeftY = this.getLocation().getX();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> combinedPredicate.and((r) -> baseLineLeftY.equals(c.getLocation().getX())));

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineLeftY, LayoutOptions.ALIGNED_VERTICALLY_LEFT),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineLeftY, LayoutOptions.ALIGNED_VERTICALLY_LEFT));
    }

    public FinishValidationBuilder alignedHorizontallyAll(LayoutComponent... layoutComponents) {
        Integer baseLineTopY = this.getLocation().getY();
        Integer baseLineBottomY = this.getLocation().getY() + this.getSize().getHeight();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var topY = c.getLocation().getY();
            var bottomY = c.getLocation().getY() + c.getSize().getHeight();
            combinedPredicate.and((r) -> baseLineTopY.equals(topY)).and((r) -> baseLineBottomY.equals(bottomY));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_TOP) +
                        buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineBottomY, LayoutOptions.ALIGNED_HORIZONTALLY_BOTTOM),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_TOP) +
                        buildFailedAlignValidationMessage(comparingComponentsNames, baseLineBottomY, LayoutOptions.ALIGNED_HORIZONTALLY_BOTTOM));
    }

    public FinishValidationBuilder alignedHorizontallyCentered(LayoutComponent... layoutComponents) {
        Integer baseLineTopY = this.getLocation().getY() + this.getSize().getHeight() / 2;
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var bottomY = c.getLocation().getY() + c.getSize().getHeight() / 2;
            combinedPredicate.and((r) -> baseLineTopY.equals(bottomY));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_CENTERED),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_CENTERED));
    }

    public FinishValidationBuilder alignedHorizontallyTop(LayoutComponent... layoutComponents) {
        Integer baseLineTopY = this.getLocation().getY();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> combinedPredicate.and((r) -> baseLineTopY.equals(c.getLocation().getY())));

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_TOP),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineTopY, LayoutOptions.ALIGNED_HORIZONTALLY_TOP));
    }

    public FinishValidationBuilder alignedHorizontallyBottom(LayoutComponent... layoutComponents) {
        Integer baseLineBottomY = this.getLocation().getY() + this.getSize().getHeight();
        var comparingComponentsNames = getLayoutComponentsNames(layoutComponents);
        Predicate combinedPredicate = (s) -> true;
        Arrays.stream(layoutComponents).forEach(c -> {
            var bottomY = c.getLocation().getY() + c.getSize().getHeight();
            combinedPredicate.and((r) -> baseLineBottomY.equals(bottomY));
        });

        return new FinishValidationBuilder(combinedPredicate,
                () -> buildNotificationAlignValidationMessage(comparingComponentsNames, baseLineBottomY, LayoutOptions.ALIGNED_HORIZONTALLY_BOTTOM),
                () -> buildFailedAlignValidationMessage(comparingComponentsNames, baseLineBottomY, LayoutOptions.ALIGNED_HORIZONTALLY_BOTTOM));
    }

    public LayoutPreciseValidationBuilder height() {
        return new LayoutPreciseValidationBuilder(this.getSize().getHeight());
    }

    public LayoutPreciseValidationBuilder width() {
        return new LayoutPreciseValidationBuilder(this.getSize().getHeight());
    }

    private String getLayoutComponentsNames(LayoutComponent[] layoutComponents) {
        var comparingComponentsNames = Arrays.stream(layoutComponents).skip(0).map(LayoutComponent::getComponentName).collect(Collectors.joining(","));
        return comparingComponentsNames;
    }

    private String buildNotificationAlignValidationMessage(String componentNames, Integer expected, LayoutOptions validationType) {
        return String.format("validate %s is %s %s %d px ", this.getComponentName(), validationType, componentNames, expected);
    }

    private String buildFailedAlignValidationMessage(String componentNames, Integer expected, LayoutOptions validationType) {
        return String.format("%s should be %s %s %d px but was not. ", this.getComponentName(), validationType, componentNames, expected);
    }

    private String buildNotificationValidationMessage(LayoutComponent secondLayoutComponent, LayoutOptions validationType) {
        return String.format("validate %s is %s of %s ", this.getComponentName(), validationType, secondLayoutComponent.getComponentName());
    }

    private String buildFailedValidationMessage(LayoutComponent secondLayoutComponent, LayoutOptions validationType) {
        return String.format("%s should be %s of %s ", this.getComponentName(), validationType, secondLayoutComponent.getComponentName());
    }

    private double calculateRightOfDistance(LayoutComponent component, LayoutComponent secondComponent) {
        return secondComponent.getLocation().getX() - (component.getLocation().getX() + component.getSize().getWidth());
    }

    private double calculateLeftOfDistance(LayoutComponent component, LayoutComponent secondComponent) {
        return component.getLocation().getX() - (secondComponent.getLocation().getX() + secondComponent.getSize().getWidth());
    }

    private double calculateAboveOfDistance(LayoutComponent component, LayoutComponent secondComponent) {
        return secondComponent.getLocation().getY() - (component.getLocation().getY() + component.getSize().getHeight());
    }

    private double calculateBelowOfDistance(LayoutComponent component, LayoutComponent secondComponent) {
        return component.getLocation().getY() - (secondComponent.getLocation().getY() + secondComponent.getSize().getHeight());
    }

    private double calculateTopInsideOfDistance(LayoutComponent innerComponent, LayoutComponent outerComponent) {
        return innerComponent.getLocation().getY() - outerComponent.getLocation().getY();
    }

    private double calculateBottomInsideOfDistance(LayoutComponent innerComponent, LayoutComponent outerComponent) {
        return (outerComponent.getLocation().getY() + outerComponent.getSize().getHeight()) - (innerComponent.getLocation().getY() + innerComponent.getSize().getHeight());
    }

    private double calculateLeftInsideOfDistance(LayoutComponent innerComponent, LayoutComponent outerComponent) {
        return innerComponent.getLocation().getX() - outerComponent.getLocation().getX();
    }

    private double calculateRightInsideOfDistance(LayoutComponent innerComponent, LayoutComponent outerComponent) {
        return (outerComponent.getLocation().getX() + outerComponent.getSize().getWidth()) - (innerComponent.getLocation().getX() + innerComponent.getSize().getWidth());
    }
}
