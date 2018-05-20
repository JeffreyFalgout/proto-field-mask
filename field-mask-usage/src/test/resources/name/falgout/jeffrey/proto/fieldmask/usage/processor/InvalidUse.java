package name.falgout.jeffrey.proto.fieldmask.usage.processor;

import name.falgout.jeffrey.proto.fieldmask.usage.RequiresFields;
import name.falgout.jeffrey.proto.fieldmask.usage.Test.Child;
import name.falgout.jeffrey.proto.fieldmask.usage.Test.Root;

class InvalidUse {
  void processRoot(@RequiresFields("first_child") Root root) {
    // BUG: Diagnostic contains: second_child
    System.out.println(root.getSecondChild());
  }

  interface DefaultReturnsAreChecked {
    default Child getChild(@RequiresFields("second_child") Root root) {
      // BUG: Diagnostic contains: first_child
      return root.getFirstChild();
    }
  }

  void followsLocalVariables(@RequiresFields("first_child.value") Root root) {
    Child firstChild = root.getFirstChild();
    // BUG: Diagnostic contains: description
    firstChild.getDescription();

    Child firstChildAgain = firstChild;
    // BUG: Diagnostic contains: description
    firstChildAgain.getDescription();
  }

  void followsLocalVariableReassignment(
      @RequiresFields({"first_child.value", "second_child.description"}) Root root) {
    Child child = root.getFirstChild();
    child.getValue();
    // BUG: Diagnostic contains: description
    child.getDescription();

    child = root.getSecondChild();
    // BUG: Diagnostic contains: value
    child.getValue();
    child.getDescription();

    Child secondChild = child;
    // BUG: Diagnostic contains: value
    secondChild.getValue();
  }

  void multipleAnnotatedParameters(
      @RequiresFields("first_child") Root firstRoot,
      @RequiresFields("second_child") Root secondRoot) {
    firstRoot.getFirstChild();
    secondRoot.getSecondChild();

    Root tmp = firstRoot;
    firstRoot = secondRoot;
    secondRoot = tmp;

    // BUG: Diagnostic contains: first_child
    firstRoot.getFirstChild();
    // BUG: Diagnostic contains: second_child
    secondRoot.getSecondChild();
  }

  void needsFirstChild(@RequiresFields("first_child") Root root) {}

  void needsFirstChildValue(@RequiresFields("first_child.value") Root root) {}

  void checksMethodCalls(@RequiresFields("first_child.description") Root root) {
    // BUG: Diagnostic contains: Argument does not have correct FieldMask
    needsFirstChild(root);
    // BUG: Diagnostic contains: Argument does not have correct FieldMask
    needsFirstChildValue(root);
  }
}
